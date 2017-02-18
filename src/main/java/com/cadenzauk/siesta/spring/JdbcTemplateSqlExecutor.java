/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
 */

package com.cadenzauk.siesta.spring;

import com.cadenzauk.siesta.RowMapper;
import com.cadenzauk.siesta.SqlExecutor;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class JdbcTemplateSqlExecutor implements SqlExecutor {
    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplateSqlExecutor(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public <T> List<T> query(String sql, Object[] args, RowMapper<T> rowMapper) {
        return jdbcTemplate.query(sql, args, rowMapper::mapRow);
    }

    public static JdbcTemplateSqlExecutor of(JdbcTemplate jdbcTemplate) {
        return new JdbcTemplateSqlExecutor(jdbcTemplate);
    }
}
