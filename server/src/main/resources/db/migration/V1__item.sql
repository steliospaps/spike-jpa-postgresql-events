create table ITEM (
	ID SERIAL PRIMARY KEY NOT NULL,
	NAME VARCHAR(100) NOT NULL,
	VALUE INT8 NOT NULL
);

CREATE or replace FUNCTION notify_changed_by_id() RETURNS trigger AS $$
BEGIN
	if (TG_OP = 'INSERT' OR TG_OP = 'UPDATE') 
	then 
	    -- identifier has to be lower case otherwise listen does not get it 
	    -- (LISTEN "CHANGED_ID" will get the capitalised version (pg_notify('CHANGED_ID')) but
	    --  LISTEN CHANGED_ID will not get it) 
    	PERFORM pg_notify( 'changed_by_id', TG_TABLE_NAME||':'||TG_OP||':'|| NEW.ID);
    elsif (TG_OP = 'DELETE') 
	then 
    	PERFORM pg_notify( 'changed_by_id', TG_TABLE_NAME||':'||TG_OP||':'|| OLD.ID);
    END if;
    return null;
END;
$$ LANGUAGE plpgsql;

CREATE or replace TRIGGER item_notify_changed_by_id AFTER INSERT OR UPDATE OR DELETE on ITEM
	FOR EACH ROW EXECUTE FUNCTION notify_changed_by_id();
