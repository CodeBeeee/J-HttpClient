package xin.jiangqiang.entity.request.body.impl;

import lombok.Getter;
import lombok.ToString;
import xin.jiangqiang.constants.CommonConstants;
import xin.jiangqiang.constants.HttpHeaderValue;
import xin.jiangqiang.entity.request.body.RequestBody;
import xin.jiangqiang.utils.FileUtils;
import xin.jiangqiang.utils.HttpUtils;

import java.io.File;
import java.util.*;

/**
 * 提交复杂参数(文件),必须设置contentType
 *
 * @author jiangqiang
 * @date 2021/1/3 9:49
 */
@ToString
@Getter
public class RequestFormDataBody implements RequestBody {
    private final Map<String, Object> params = new HashMap<>();//Object可以是字符串,可以是File类型,可以是List<File>类型
    private String UUID = "852863227123856829090538";//todo  需要优化为随机生成

    public String builder(String contentType) {//todo
        String separator ="----------------------------" + UUID ;//每一个参数结束的分隔符
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String name = entry.getKey();
            Object value = entry.getValue();
            stringBuilder.append(separator).append(CommonConstants.CRLF);
            stringBuilder.append("Content-Disposition").append(CommonConstants.COLON).append(CommonConstants.BLANKSPACE).append("form-data;")
                    .append(CommonConstants.BLANKSPACE).append("name=\"").append(name).append("\"");
            if (value instanceof String) {
                String val = (String) value;
                stringBuilder.append(CommonConstants.CRLF).append(CommonConstants.CRLF).append(val).append(CommonConstants.CRLF);
            } else if (value instanceof File) {//单个文件
                File val = (File) value;
                stringBuilder.append(";").append(CommonConstants.BLANKSPACE).append("filename=\"").append(val.getName()).append("\"");
                stringBuilder.append(CommonConstants.CRLF);
                stringBuilder.append("Content-Type: text/plain\r\n\r\n");
                stringBuilder.append(new String(FileUtils.fileConvertToByteArray(val))).append(CommonConstants.CRLF).append(CommonConstants.CRLF);
            } else if (value instanceof List<?>) {//列表
                List<?> list = (List<?>) value;
                for (Object next : list) {
                    if (next instanceof File) {//如果是文件
                        File file = (File) next;
                    } else {//不是文件
                        throw new RuntimeException("不支持的类型");
                    }
                }
            } else {//todo 或许可以直接传递字节数组
                throw new RuntimeException("不支持的类型");
            }
        }
        stringBuilder.append(separator).append("--").append(CommonConstants.CRLF);
        System.out.println(stringBuilder);
        return stringBuilder.toString();
    }

    public RequestFormDataBody addAllParams(Map<String, Object> params) {
        this.params.putAll(params);
        return this;
    }

    public RequestFormDataBody addParam(String name, Object value) {
        this.params.put(name, value);
        return this;
    }

    public RequestFormDataBody removeParam(String name) {
        this.params.remove(name);
        return this;
    }

    public RequestFormDataBody removeAllParams() {
        this.params.clear();
        return this;
    }

}
