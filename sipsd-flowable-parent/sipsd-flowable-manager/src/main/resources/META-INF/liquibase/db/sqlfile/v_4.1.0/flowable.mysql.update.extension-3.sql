ALTER TABLE act_ru_extension_task
    ADD COLUMN SUSPENSION_STATE_ int(11) DEFAULT 1;
ALTER TABLE act_ru_extension_task
    ADD COLUMN SUSPENSION_TIME_ datetime DEFAULT NULL;