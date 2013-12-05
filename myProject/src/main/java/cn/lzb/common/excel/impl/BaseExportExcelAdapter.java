package cn.lzb.common.excel.impl;

import cn.lzb.common.excel.ExportExcelAware;
import cn.lzb.common.excel.ExportExcelContext;
import cn.lzb.common.excel.factory.ExportExcelFactory;
import cn.lzb.common.lang.ArrayUtil;
import cn.lzb.common.lang.StringUtil;

import javax.servlet.http.HttpServletResponse;

/**
 * 功能描述：导出生成Excel文件基本Adapter
 * <p/>
 * <p>实现基本的ExportExcelAware接口方法</p>
 *
 * @author: Zhenbin.Li
 * email： zhenbin.li@okhqb.com
 * company：华强北在线
 * Date: 13-12-4 Time：上午11:05
 */
public abstract class BaseExportExcelAdapter<T> implements ExportExcelAware<T> {

    /**
     * 文件下载的header key
     */
    protected static final String RESOURCE_HEADER_KEY = "Content-Disposition";

    /**
     * 文件下载的contentType
     */
    protected static final String RESOURCE_CONTENT_TYPE = "application/octet-stream;charset=UTF-8";

    /**
     * 生成Excel文件上下文
     */
    protected ExportExcelContext<T> exportExcelContext = new ExportExcelContext<T>();

    /**
     * 创建Excel工厂
     */
    protected final ExportExcelFactory exportExcelFactory;

    /**
     * 构造方法，初始化导出文件上线问
     */
    public BaseExportExcelAdapter() {

        // 初始化上下文参数
        init(exportExcelContext);

        // 验证上下文参数
        doValidContext(exportExcelContext);

        // 创建生产Excel文件工厂
        exportExcelFactory = new ExportExcelFactory(exportExcelContext.getSheetName());
    }

    /**
     * 初始化导出文件上下文
     *
     * @param exportContext
     */
    public abstract void init(ExportExcelContext<T> exportContext);

    /**
     * 校验上下文参数
     *
     * @param exportContext
     */
    @Override
    public void doValidContext(ExportExcelContext<T> exportContext) {

        if (StringUtil.isBlank(exportContext.getFileName())) {
            throw new RuntimeException("生成Excel文件名称为空");
        }
        if (ArrayUtil.isEmpty(exportContext.getColTitles())) {
            throw new RuntimeException("生成Excel文件名称为空");
        }
        if (exportContext.getHttpServletResponse() == null
                && StringUtil.isBlank(exportExcelContext.getFilePath())) {
            throw new RuntimeException(
                    "HttpServletResponse与filePath不能同时为空，HttpServletResponse为空时filePath不能为空");
        }
    }

    /**
     * 创建生成文件
     */
    public abstract void create();

    /**
     * 获取Excel表格宽度
     *
     * @return
     */
    public int getColumnSize() {

        return exportExcelContext.getColTitles()[0].length;
    }

    /**
     * 写入下载响应头信息
     * <p>声明响应类型和内容类型</p>
     *
     * @param fileName 文件名
     * @param response HTTP响应
     */
    protected static void writeDownloadResHeader(String fileName, HttpServletResponse response) {
        response.addHeader(RESOURCE_HEADER_KEY, "attachment; filename=" + fileName);
        response.setContentType(RESOURCE_CONTENT_TYPE);
    }
}
