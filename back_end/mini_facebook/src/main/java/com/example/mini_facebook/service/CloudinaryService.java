package com.example.mini_facebook.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.mini_facebook.service.impl.ICloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService  implements ICloudinaryService {
    @Autowired
    private Cloudinary cloudinary;
    @Override
    public String uploadFIle(MultipartFile file) throws IOException {
//        Map upload = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap()); // ObjectUtils.emptyMap() tạo map rỗng với thông tin mặc định(mặc định là ảnh)
//        return upload.get("url").toString();
        Map upload = cloudinary.uploader().uploadLarge( //  uploadLarge: file lớn / upload: file nhỏ
                file.getBytes(), // trả về mảng byte vì cloudinary chỉ nhận byte
                ObjectUtils.asMap( // tạo map nhanh và chỉ định thông tin lưu
                        "resource_type", "auto",  // tự nhận dạng ảnh/video
                        "folder", "mini_facebook" // thư mục trên cloudinary
                )
        );
        return upload.get("secure_url").toString(); // lấy url an toàn
    }
}
