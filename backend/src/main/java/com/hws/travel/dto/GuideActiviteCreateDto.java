package com.hws.travel.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuideActiviteCreateDto {
    private Long activiteId;
    private int jour;
    private int ordre;
}
