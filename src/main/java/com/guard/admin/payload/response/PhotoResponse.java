package com.guard.admin.payload.response;

import com.guard.admin.database.entities.Photo;
import lombok.Data;

@Data
public class PhotoResponse {
    private Photo photo;
}
