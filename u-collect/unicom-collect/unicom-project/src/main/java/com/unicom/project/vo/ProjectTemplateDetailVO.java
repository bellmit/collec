package com.unicom.project.vo;

import com.unicom.project.entity.ProjectTemplateEntity;
import com.unicom.project.entity.ProjectTemplateItemEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author yangpeng
 */
@Data
@AllArgsConstructor
public class ProjectTemplateDetailVO {
    private ProjectTemplateEntity project;


    private List<ProjectTemplateItemEntity> projectItems;

}
