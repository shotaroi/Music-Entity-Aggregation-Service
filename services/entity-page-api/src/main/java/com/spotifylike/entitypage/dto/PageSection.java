package com.spotifylike.entitypage.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PageSection(String type, List<SectionItem> items) {}
