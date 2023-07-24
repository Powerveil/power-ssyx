package com.power.ssyx;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Powerveil
 * @Date 2023/7/23 20:33
 */
@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AA {

    @ApiModelProperty(name = "name")
    Integer id;

    @ApiModelProperty(name = "name")
    String name;

    @ApiModelProperty(name = "my_id")
    int my_id;

    @ApiModelProperty(name = "list", hidden = true)
    List<String> list;

}
