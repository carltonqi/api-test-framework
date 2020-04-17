package com.merico.inftest.filter;

import com.google.common.base.Splitter;
import com.merico.inftest.cases.TestCaseInfoNew;

import org.apache.commons.lang3.StringUtils;
import org.testng.collections.Lists;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class CaseTagsFilters implements CaseFilter {

    List<String> tags = Lists.newArrayList();


    public CaseTagsFilters(String ids) {
        if (StringUtils.isBlank(ids)) {
            this.tags = Collections.EMPTY_LIST;
        }
        this.tags = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(ids);

    }

    @Override
    public void filter(List<TestCaseInfoNew> testCaseInfos) {
        if (tags.size() == 1 && tags.contains("*")) {
            return;
        }

        Iterator<TestCaseInfoNew> iterator = testCaseInfos.iterator();

        while (iterator.hasNext()) {
            TestCaseInfoNew next = iterator.next();
            if (!tags.contains(next.getTag())) {
                iterator.remove();
            }
        }
    }
}
