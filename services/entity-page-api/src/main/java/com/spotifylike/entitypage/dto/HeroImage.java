package com.spotifylike.entitypage.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record HeroImage(String url, Integer w, Integer h) {}
