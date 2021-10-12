package com.unicom.project.vo;

import com.unicom.project.entity.UserProjectEntity;
import com.unicom.project.entity.UserProjectItemEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author smalljop
 */
@Data
@AllArgsConstructor
public class UserProjectDetailVO {
    private UserProjectEntity project;


    private List<UserProjectItemEntity> projectItems;

    private UserProjectThemeVo userProjectTheme;
}
