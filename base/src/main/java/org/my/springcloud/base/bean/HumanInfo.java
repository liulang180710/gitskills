package org.my.springcloud.base.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HumanInfo implements Serializable {
    private String humanName;
    private int  humanID;
    private int regionType;
    private String regionName;
    private int unitID;
    private String unitName;
    private int age;
    private String telMobile;
}
