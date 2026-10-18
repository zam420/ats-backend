package com.deepconnect.ats.service;

import com.deepconnect.ats.model.TableButtonConfig;
import com.deepconnect.ats.model.TableHeaderConfig;
import com.deepconnect.ats.model.Tag;
import com.deepconnect.ats.repository.TableButtonConfigRepository;
import com.deepconnect.ats.repository.TableHeaderConfigRepository;
import com.deepconnect.ats.repository.TagRepository;
import com.deepconnect.ats.service.auth.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommonService {

    private final TableHeaderConfigRepository tableHeaderConfigRepository;
    private final TableButtonConfigRepository tableButtonConfigRepository;
    private final TagRepository tagRepository;

    public List<TableHeaderConfig> getTableHeaderConfig(String table) {
        Long userId = AuthService.getLoggedInUserId();
        if (userId == null) userId = 1L;
        return tableHeaderConfigRepository.findByUserIdAndTableOrderBySortAsc(userId, table);
    }

    @Transactional
    public void saveTableHeaderConfig(String table, List<TableHeaderConfig> configs) {
        Long userId = AuthService.getLoggedInUserId();
        if (userId == null) userId = 1L;

        tableHeaderConfigRepository.deleteByUserIdAndTable(userId, table);
        for (TableHeaderConfig c : configs) {
            c.setUserId(userId);
            c.setTable(table);
            tableHeaderConfigRepository.save(c);
        }
    }

    public List<TableButtonConfig> getTableButtonConfig(String table) {
        Long userId = AuthService.getLoggedInUserId();
        if (userId == null) userId = 1L;
        return tableButtonConfigRepository.findByUserIdAndTableOrderBySortAsc(userId, table);
    }

    @Transactional
    public void saveTableButtonConfig(String table, List<TableButtonConfig> configs) {
        Long userId = AuthService.getLoggedInUserId();
        if (userId == null) userId = 1L;

        tableButtonConfigRepository.deleteByUserIdAndTable(userId, table);
        for (TableButtonConfig c : configs) {
            c.setUserId(userId);
            c.setTable(table);
            tableButtonConfigRepository.save(c);
        }
    }

    public List<Tag> listAllTags() {
        return tagRepository.findAll();
    }

    @Transactional
    public Long saveTag(Tag tag) {
        Long userId = AuthService.getLoggedInUserId();
        if (userId == null) userId = 1L;
        tag.setCreatedById(userId);
        return tagRepository.save(tag).getId();
    }

    @Transactional
    public void deleteTag(Long id) {
        tagRepository.deleteById(id);
    }
}
