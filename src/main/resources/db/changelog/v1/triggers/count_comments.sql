CREATE OR REPLACE FUNCTION fn_count_comments() RETURNS TRIGGER AS $$
    BEGIN
        IF (TG_OP = 'DELETE') THEN
            UPDATE task SET comments_count = comments_count - 1
            WHERE task.id = OLD.task_id;
            RETURN OLD;
        ELSIF (TG_OP = 'INSERT') THEN
            UPDATE task SET comments_count = comments_count + 1
            WHERE task.id = NEW.task_id;
            RETURN NEW;
        END IF;
        RETURN NULL;
    END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tg_count_attachments
AFTER INSERT OR DELETE ON comment
FOR EACH ROW EXECUTE PROCEDURE fn_count_comments();