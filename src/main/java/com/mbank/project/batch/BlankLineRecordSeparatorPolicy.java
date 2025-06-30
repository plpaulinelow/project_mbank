package com.mbank.project.batch;

import org.springframework.batch.item.file.separator.SimpleRecordSeparatorPolicy;

/*
 * Class to handle empty line when reading
 */
public class BlankLineRecordSeparatorPolicy extends SimpleRecordSeparatorPolicy {

    @Override
    public boolean isEndOfRecord(final String line) {
        return line.trim().length() != 0 && super.isEndOfRecord(line);
    }

    @Override
    public String postProcess(final String record) {
        if (record == null || record.trim().length() == 0) {
            return null;
        }
        return super.postProcess(record);
    }

}
