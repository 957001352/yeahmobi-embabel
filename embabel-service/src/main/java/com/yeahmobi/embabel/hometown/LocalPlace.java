package com.yeahmobi.embabel.hometown;

import com.embabel.ux.form.Text;
import com.fasterxml.jackson.annotation.JsonClassDescription;

@JsonClassDescription("一个人的籍贯（家乡/出生地）细节")
public record LocalPlace(@Text(label = "homeTown") String homeTown) {
}