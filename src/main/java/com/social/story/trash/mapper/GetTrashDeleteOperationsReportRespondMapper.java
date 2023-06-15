package com.social.story.trash.mapper;

import com.social.story.mappers.ObjectMapper;
import com.social.story.trash.model.Trash;
import com.social.swagger.model.contentmanagement.GetDeleteOperationsReportRespond;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ayameen
 */
@AllArgsConstructor
@Service
public class GetTrashDeleteOperationsReportRespondMapper implements ObjectMapper<Page<Trash>, GetDeleteOperationsReportRespond> {

    TrashDetailsMapper trashDetailsMapper;

    @Override
    public GetDeleteOperationsReportRespond toTarget(Page<Trash> source) {
        GetDeleteOperationsReportRespond getDeleteOperationsReportRespond = new GetDeleteOperationsReportRespond();
        getDeleteOperationsReportRespond.setTrashList(trashDetailsMapper.toTargets(source.getContent()));
        getDeleteOperationsReportRespond.setPageNumber(source.getNumber()+1);
        getDeleteOperationsReportRespond.setPageSize(source.getSize());
        getDeleteOperationsReportRespond.setTotalPages(source.getTotalPages());
        getDeleteOperationsReportRespond.setTotalElements(source.getTotalElements());
        return getDeleteOperationsReportRespond;
    }

    @Override
    public List<GetDeleteOperationsReportRespond> toTargets(List<Page<Trash>> sourceList) {
        return sourceList == null ? Collections.emptyList() : sourceList.stream().map(this::toTarget).collect(Collectors.toList());
    }

    @Override
    public Page<Trash> toSource(GetDeleteOperationsReportRespond target) {
        return null;
    }

    @Override
    public List<Page<Trash>> toSources(List<GetDeleteOperationsReportRespond> targetsList) {
        return Collections.emptyList();
    }
}
