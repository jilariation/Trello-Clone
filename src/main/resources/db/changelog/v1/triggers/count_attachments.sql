CREATE OR REPLACE FUNCTION fn_count_attachments() RETURNS TRIGGER AS $$
BEGIN
    IF (TG_OP = 'DELETE') THEN
        UPDATE task SET attachments_count = attachments_count - 1
        WHERE task.id = OLD.task_id;
        RETURN OLD;
    ELSIF (TG_OP = 'INSERT') THEN
        UPDATE task SET attachments_count = attachments_count + 1
        WHERE task.id = NEW.task_id;
        RETURN NEW;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tg_count_attachments
    AFTER INSERT OR DELETE ON task_attachment
    FOR EACH ROW EXECUTE PROCEDURE fn_count_attachments();