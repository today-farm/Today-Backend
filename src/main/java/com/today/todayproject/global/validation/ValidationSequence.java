package com.today.todayproject.global.validation;

import javax.validation.GroupSequence;
import javax.validation.groups.Default;

import static com.today.todayproject.global.validation.ValidationGroups.*;

@GroupSequence({Default.class, NotBlankGroup.class, PatternGroup.class, SizeGroup.class})
public interface ValidationSequence {
}
