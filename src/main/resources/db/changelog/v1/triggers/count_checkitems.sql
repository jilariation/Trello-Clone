CREATE OR REPLACE FUNCTION fn_count_checkitems() RETURNS TRIGGER AS $$
BEGIN
    IF (TG_OP = 'DELETE') THEN
        UPDATE task SET checkitems_count = checkitems_count - 1,
                        checkitems_checked_count = checkitems_checked_count - 1
        WHERE task.id = (SELECT task_id FROM checklist WHERE id = OLD.checklist_id);
        RETURN OLD;
    ELSIF (TG_OP = 'INSERT') THEN
        UPDATE task SET checkitems_count = checkitems_count + 1,
                        checkitems_checked_count = checkitems_checked_count + CASE WHEN NEW.is_checked THEN 1 ELSE 0 END
        WHERE task.id = (SELECT task_id FROM checklist WHERE id = NEW.checklist_id);
        RETURN NEW;
    ELSIF (TG_OP = 'UPDATE') THEN
          IF OLD.is_checked IS DISTINCT FROM NEW.is_checked THEN
            UPDATE task SET checkitems_checked_count = checkitems_checked_count + CASE WHEN NEW.is_checked THEN 1 ELSE -1 END
            WHERE task.id = (SELECT task_id FROM checklist WHERE id = NEW.checklist_id);
            RETURN NEW;
          END IF;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tg_count_checkitems
    AFTER INSERT OR DELETE OR UPDATE ON checkitem
    FOR EACH ROW EXECUTE PROCEDURE fn_count_checkitems();