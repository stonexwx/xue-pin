package cn.org.qsmx.controller;

import cn.org.qsmx.MinIOConfig;
import cn.org.qsmx.MinIOUtils;
import cn.org.qsmx.pojo.bo.Base64FileBO;
import cn.org.qsmx.result.GraceJSONResult;
import cn.org.qsmx.result.ResponseStatusEnum;
import cn.org.qsmx.util.Base64ToFile;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.util.UUID;

@RestController
@RequestMapping("file")
public class FileController {
    @Autowired
    private MinIOConfig minIOConfig;

    @PostMapping("uploadFace")
    public GraceJSONResult uploadFace(@RequestParam("file") MultipartFile file,
                                      @RequestParam("userId") String userId
    ) throws Exception {
        if (StringUtils.isBlank(userId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }

        String fileName = file.getOriginalFilename();
        if (StringUtils.isBlank(fileName)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_NULL_ERROR);
        }

        fileName = userId + "/" + fileName;

        MinIOUtils.uploadFile(minIOConfig.getBucketName(), fileName, file.getInputStream());

        String imageUrl = minIOConfig.getFileHost() + "/" + minIOConfig.getBucketName() + "/" + fileName;

        return GraceJSONResult.ok(imageUrl);
    }

    /**
     * 上传base64文件
     * @param base64FileBO
     * @return
     * @throws Exception
     */
    @PostMapping("uploadAdminFace")
    public GraceJSONResult uploadAdminFace(@RequestBody @Valid Base64FileBO base64FileBO) throws Exception {
        String base64File = base64FileBO.getBase64File();

        String suffixName = ".png";//后缀名
        String uuid = UUID.randomUUID().toString();//文件名
        String objectName = uuid + suffixName;//对象名

        String rootPath = "D:\\face"+ File.separator;
        String filePath = rootPath +File.separator+ "adminFace"+File.separator+objectName;
        Base64ToFile.Base64ToFile(base64File,filePath);

        MinIOUtils.uploadFile(minIOConfig.getBucketName(), objectName, filePath);

        String imageUrl = minIOConfig.getFileHost() + "/" + minIOConfig.getBucketName() + "/" + objectName;
        return GraceJSONResult.ok(imageUrl);
    }
}
