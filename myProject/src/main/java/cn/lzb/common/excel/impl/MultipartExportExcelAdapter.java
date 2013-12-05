package cn.lzb.common.excel.impl;

import cn.lzb.common.lang.ArrayUtil;
import cn.lzb.common.lang.DateUtil;
import cn.lzb.common.lang.StringUtil;
import com.google.common.collect.Lists;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 功能描述：复杂Excel文件生成Adapter
 *
 * @author: Zhenbin.Li
 * email： zhenbin.li@okhqb.com
 * company：华强北在线
 * Date: 13-12-4 Time：上午10:30
 */
public abstract class MultipartExportExcelAdapter<T> extends BaseExportExcelAdapter<T> {

    /**
     * 生成Excel文件主体数据集合
     */
    protected List<String[][]> excelData = Lists.newArrayList();

    /**
     * 构造方法
     */
	public MultipartExportExcelAdapter() {
        synchronized (this) {
            handleExportData(exportExcelContext.getExports(), excelData);
            create();
        }
    }

    /**
     * 业务对象集合转换为String[][]集合，数据转换为Excel导出数据
     * 需要合并时数组格式为
     * <p/>
     * <p/>
     * <p>二维数组格式：
     *      [00001][2][3][0004][5]
     *      [null][7][8][null][5]
     *      [null][7][8][null][5]
     * </p>
     *
     * @param list      业务对象集合
     * @param excelData 生成Excel文件数据数组集合
     */
    public abstract void handleExportData(List<T> list, List<String[][]> excelData);

    /**
     * 创建Excel文件
     */
    @Override
    public void create() {

        // 列头标题
        String[][] colTitles = exportExcelContext.getColTitles();
        exportExcelFactory.setCols(colTitles[0].length);

        // 表格表头
        String title = exportExcelContext.getTitle();
        if (StringUtil.isNotBlank(title)) {
            // 表格名称
            StringBuffer titleBuffer = new StringBuffer();
            titleBuffer.append(title).append("(导出时间：").append(DateUtil.formatDateTime()).append(")");
            exportExcelFactory.createCaption(titleBuffer.toString());
        }

        // 列
        exportExcelFactory.createColCaption(colTitles);

        // 处理导出Excel文件数据
        exportExcelFactory.createMultipleBody(excelData);

        // 列宽
        int[] columnWidths = exportExcelContext.getColumnWidths();
        if (ArrayUtil.isNotEmpty(columnWidths)) {
            exportExcelFactory.setColumnWidth(columnWidths);
        }

        // 备注
        String[] remarks = exportExcelContext.getRemarks();
        if (ArrayUtil.isNotEmpty(remarks)) {
            exportExcelFactory.createRemarks(remarks);
        }

        // 生成文件名称
        String excelFileName = exportExcelContext.getFileName() + ".xls";

        // 写文件
        if (exportExcelContext.getHttpServletResponse() != null) {

            try {
                excelFileName = new String(excelFileName.getBytes("GBK"), "ISO-8859-1");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("导出Excel业务， encode文件名称异常, excelFileName=" + excelFileName, e);
            }

            HttpServletResponse response = exportExcelContext.getHttpServletResponse();
            response.reset();

            // 写入下载响应头信息
            OutputStream os = null;
            writeDownloadResHeader(excelFileName, response);
            try {
                os = response.getOutputStream();
                exportExcelFactory.createFile(response.getOutputStream());
            } catch (IOException e) {
                throw new RuntimeException("导出Excel业务， 生成Excel文件异常", e);
            } finally {
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {
                    }
                }
            }
        } else {
            // 直接创建生成文件
            exportExcelFactory.createFile(exportExcelContext.getFilePath() + excelFileName);
        }
    }
}
