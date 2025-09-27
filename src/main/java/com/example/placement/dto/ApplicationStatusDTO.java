package com.example.placement.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationStatusDTO {
    private String label;
    private int value;
    private String color;
    private double percent;
}