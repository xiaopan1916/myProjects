package com.hqb360.common.excel;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * 功能描述：创建生成Excel工厂
 *
 * @author: Zhenbin.Li
 * email： zhenbin.li@okhqb.com
 * company：华强北在线
 * Date: 13-11-19 Time：下午12:46
 */
public class ExportExcelFactory {

    /**
     * 工作薄
     */
    private HSSFWorkbook workbook;

    /**
     * 工作表
     */
    private HSSFSheet sheet;

    /**
     * 当前行数
     */
    private int currentRow = 0;

    /**
     * 总列数
     */
    private int totalCols;

    /**
     * Excel Sheet名称
     */
    private final String sheetName;

    /**
     * 默认构造方法
     */
    public ExportExcelFactory(String sheetName) {
        this.sheetName = sheetName;
        workbook = new HSSFWorkbook();
        sheet = workbook.createSheet(getSheetName());
    }

    /**
     * 设置列宽
     *
     * @param columnWidth 列宽信息
     */
    @SuppressWarnings("deprecation")
    public void setColumnWidth(int[] columnWidth) {
        for (int i = 0; i < columnWidth.length; i++) {
            if (columnWidth[i] != 0) {
                if (sheet.getColumnWidth((short) i) != (columnWidth[i] * 37.5)) {
                    sheet.setColumnWidth((short) i, (short) (columnWidth[i] * 37.5));
                }
            }
        }
    }

    /**
     * 设置列数
     *
     * @param cols 列数
     */
    public void setCols(int cols) {
        this.totalCols = cols;
    }

    /**
     * 创建标题
     *
     * @param caption 标题
     */
    public void createCaption(String caption) {
        HSSFFont font = ExportExcelUtil.getFont(workbook, 10, HSSFFont.BOLDWEIGHT_BOLD, HSSFColor.BLACK.index);
        HSSFCellStyle style = ExportExcelUtil.getStyle(workbook, font, HSSFCellStyle.ALIGN_CENTER);

        HSSFRow row = sheet.createRow(currentRow);
        ExportExcelUtil.createCell(row, 0, style, caption);
        for (int i = 1; i < totalCols; i++) {
            ExportExcelUtil.createCell(row, i, style, "");
        }

        ExportExcelUtil.mergeCol(sheet, 0, totalCols - 1, currentRow);
        currentRow++;
    }

    /**
     * 创建一行列标题
     *
     * @param colCaption 一行列标题
     */
    public void createColCaption(String[] colCaption) {
        HSSFFont font = ExportExcelUtil.getFont(workbook, 0, HSSFFont.BOLDWEIGHT_BOLD, HSSFColor.BLACK.index);
        HSSFCellStyle style = ExportExcelUtil.getStyle(workbook, font, HSSFCellStyle.ALIGN_CENTER);

        HSSFRow row = sheet.createRow(currentRow);
        for (int i = 0; i < colCaption.length; i++) {
            ExportExcelUtil.createCell(row, i, style, colCaption[i]);
        }
        currentRow++;
    }

    /**
     * 创建多行列标题
     *
     * @param colCaption 多行列标题
     */
    public void createColCaption(String[][] colCaption) {
        HSSFFont font = ExportExcelUtil.getFont(workbook, 10, HSSFFont.BOLDWEIGHT_BOLD, HSSFColor.BLACK.index);
        HSSFCellStyle style = ExportExcelUtil.getStyle(workbook, font, HSSFCellStyle.ALIGN_CENTER);
        int[][][] spans = getSpans(colCaption);

        for (int i = 0; i < colCaption.length; i++) {
            HSSFRow row = sheet.createRow(currentRow);
            for (int j = 0; j < colCaption[i].length; j++) {
                ExportExcelUtil.createCell(row, j, style, colCaption[i][j]);
                if (spans[i][j] != null) {
                    ExportExcelUtil
                            .mergeRegion(sheet, currentRow, j, currentRow + spans[i][j][0] - 1, j + spans[i][j][1] - 1);
                }
            }
            currentRow++;
        }
    }

    /**
     * 创建EXCEL的主体数据
     *
     * @param data 主体数据，可支持的数据格式有：
     *             1、简单列表 List(String[])；
     *             2、合并一列 Map(List(String[]))；
     *             3、合并二列 Map(Map(List(String[]))。
     */
    @SuppressWarnings("rawtypes")
    public void createBody(Object data) {
        int nestedCount = ExportExcelUtil.getNestedCount(data);

        if (nestedCount == 0) {
            createBody((List<String[]>) data);
        } else if (nestedCount == 1) {
            ExportExcelUtil.createOneMapBody(workbook, sheet, (Map) data, currentRow);
        } else if (nestedCount == 2) {
            ExportExcelUtil.createTwoMapBody(workbook, sheet, (Map) data, currentRow);
        } else {
            throw new IllegalArgumentException("不支持的数据格式");
        }
    }

    /**
     * 创建列表的主体数据
     *
     * @param list List(String[])列表数据
     */
    protected void createBody(List<String[]> list) {

        HSSFFont font = ExportExcelUtil.getFont(workbook, 10, HSSFFont.BOLDWEIGHT_NORMAL, HSSFColor.BLACK.index);
        HSSFCellStyle style = ExportExcelUtil.getStyle(workbook, font, HSSFCellStyle.ALIGN_CENTER);

        for (int index = 0; index < list.size(); index++) {
            HSSFRow row = sheet.createRow(currentRow);
            String[] stringArray = list.get(index);
            for (int j = 0; j < stringArray.length; j++) {
                ExportExcelUtil.createCell(row, j, style, stringArray[j]);
            }
            currentRow++;
        }
    }

    /**
     * 创建EXCEL的主体数据
     *
     * @param data 主体数据，可支持的数据格式有：
     *             1、简单列表 List(String[])；
     *             2、合并一列 Map(List(String[]))；
     *             3、合并二列 Map(Map(List(String[]))。
     */
    @SuppressWarnings("rawtypes")
    public void createBody(Object data, Object color) {
        int nestedCount = ExportExcelUtil.getNestedCount(data);

        if (nestedCount == 0) {
            ExportExcelUtil.createBodyColor(workbook, sheet, (List) data, (Map) color, currentRow);
        } else if (nestedCount == 1) {
            ExportExcelUtil.createOneMapBody(workbook, sheet, (Map) data, currentRow);
        } else if (nestedCount == 2) {
            ExportExcelUtil.createTwoMapBody(workbook, sheet, (Map) data, currentRow);
        } else {
            throw new IllegalArgumentException("不支持的数据格式");
        }
    }

    /**
     * 创建备注信息
     *
     * @param remarks 备注信息
     */
    public void createRemarks(String[] remarks) {
        HSSFFont font = ExportExcelUtil.getFont(workbook, 10, HSSFFont.BOLDWEIGHT_NORMAL, HSSFColor.RED.index);
        HSSFCellStyle style = ExportExcelUtil.getStyle(workbook, font, HSSFCellStyle.ALIGN_LEFT);

        for (int i = 0; i < remarks.length; i++) {
            HSSFRow row = sheet.createRow(currentRow);
            ExportExcelUtil.createCell(row, 0, style, remarks[i]);

            for (int j = 1; j < totalCols; j++) {
                ExportExcelUtil.createCell(row, j, style, "");
            }

            ExportExcelUtil.mergeCol(sheet, 0, totalCols - 1, currentRow);
            currentRow++;
        }
    }

    /**
     * 创建本地路径的EXCEL文件
     *
     * @param filePath EXCEL文件路径
     */
    public void createFile(String filePath) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filePath);
            workbook.write(out);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("无法创建文件：" + filePath);
        } catch (IOException e) {
            throw new RuntimeException("无法写入文件：" + filePath);
        } finally {
            ExportExcelUtil.closeOutputStream(out);
        }
    }

    /**
     * 把EXCEL文件写入到输出流中
     *
     * @param out 输出流
     * @throws java.io.IOException
     */
    public void createFile(OutputStream out) throws IOException {
        workbook.write(out);
    }


    /**
     * 根据多行报表标题获得一个合并的行列信息
     *
     * @param colTitles 列标题
     * @return 报表标题合并的行列信息：第一维对应标题的行，第二维对应标题的列，
     *         第三维对应标题的合并行和列，已经被合并的行或列则对应的第三维为null。
     */
    public static int[][][] getSpans(String[][] colTitles) {
        int[][][] spans = new int[colTitles.length][][];

        for (int i = 0; i < colTitles.length; i++) {
            spans[i] = new int[colTitles[i].length][];
            for (int j = 0; j < colTitles[i].length; j++) {
                if (colTitles[i][j] != null) {
                    spans[i][j] = new int[2];
                    spans[i][j][0] = ExportExcelUtil.getRowSpan(colTitles, i, j);
                    spans[i][j][1] = ExportExcelUtil.getColSpan(colTitles, i, j);
                }
            }
        }

        return spans;
    }

    /**
     * 获取Excel Sheet名称
     *
     * @return
     */
    public String getSheetName() {
        return sheetName;
    }
}
