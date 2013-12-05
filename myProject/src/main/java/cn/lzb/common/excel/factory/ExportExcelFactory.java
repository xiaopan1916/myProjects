package cn.lzb.common.excel.factory;

import cn.lzb.common.lang.StringUtil;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * 功能描述：创建生成Excel文件工厂
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
        if (StringUtil.isNotBlank(sheetName)) {
            this.sheetName = sheetName;
        } else {
            this.sheetName = "sheet";
        }
        workbook = new HSSFWorkbook();
        sheet = workbook.createSheet(getSheetName());
    }

    /**
     * 创建合并Excel列列表的主体数据
     *
     * @param list List(String[][])列表数据
     */
    public void createMultipleBody(List<String[][]> list) {

        HSSFFont font = ExportExcelUtil.getFont(workbook, 10, HSSFFont.BOLDWEIGHT_NORMAL, HSSFColor.BLACK.index);
        HSSFCellStyle style = ExportExcelUtil.getStyle(workbook, font, HSSFCellStyle.ALIGN_CENTER);

        for (String[][] excel : list) {
            int[][][] spans = ExportExcelUtil.getSpans(excel);
            for (int i = 0; i < excel.length; i++) {
                HSSFRow row = sheet.createRow(currentRow);
                for (int j = 0; j < excel[i].length; j++) {
                    ExportExcelUtil.createCell(row, j, style, excel[i][j]);
                    if (excel[i][j] != null) {
                        ExportExcelUtil.mergeRegion(
                                sheet, currentRow, j, currentRow + spans[i][j][0] - 1, j + spans[i][j][1] - 1);
                    }
                }
                currentRow++;
            }
        }
    }

    /**
     * 创建列表的主体数据
     *
     * @param list List(String[])列表数据
     */
    public void createBody(List<String[]> list) {

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
     * 创建列表的主体数据，包含单元格颜色样式
     *
     * @param list  List(String[])列表数据
     * @param color 单元格颜色样式
     */
    public void createBodyColor(List<String[]> list, Map<Integer, String> color) {

        HSSFFont font_black = ExportExcelUtil.getFont(workbook, 10, HSSFFont.BOLDWEIGHT_NORMAL, HSSFColor.BLACK.index);
        HSSFCellStyle style_black = ExportExcelUtil.getStyle(workbook, font_black, HSSFCellStyle.ALIGN_CENTER);
        HSSFFont font_red = ExportExcelUtil.getFont(workbook, 10, HSSFFont.BOLDWEIGHT_NORMAL, HSSFColor.RED.index);
        HSSFCellStyle style_red = ExportExcelUtil.getStyle(workbook, font_red, HSSFCellStyle.ALIGN_CENTER);
        HSSFFont font_green = ExportExcelUtil.getFont(workbook, 10, HSSFFont.BOLDWEIGHT_NORMAL, HSSFColor.GREEN.index);
        HSSFCellStyle style_green = ExportExcelUtil.getStyle(workbook, font_green, HSSFCellStyle.ALIGN_CENTER);

        for (int index = 0; index < list.size(); index++) {
            String rowColor = color.get(index);
            HSSFRow row = sheet.createRow(currentRow);
            String[] stringArray = list.get(index);
            for (int j = 0; j < stringArray.length; j++) {
                if (rowColor != null) {
                    if (rowColor.equals(ExportFrontColor.RED.getColorCode())) {
                        ExportExcelUtil.createCell(row, j, style_red, stringArray[j]);
                    } else if (rowColor.equals(ExportFrontColor.GREEN.getColorCode())) {
                        ExportExcelUtil.createCell(row, j, style_green, stringArray[j]);
                    } else {
                        ExportExcelUtil.createCell(row, j, style_black, stringArray[j]);
                    }
                } else {
                    ExportExcelUtil.createCell(row, j, style_black, stringArray[j]);
                }
            }
            currentRow++;
        }
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
        int[][][] spans = ExportExcelUtil.getSpans(colCaption);

        for (int i = 0; i < colCaption.length; i++) {
            HSSFRow row = sheet.createRow(currentRow);
            for (int j = 0; j < colCaption[i].length; j++) {
                ExportExcelUtil.createCell(row, j, style, colCaption[i][j]);
                if (colCaption[i][j] != null) {
                    ExportExcelUtil
                            .mergeRegion(sheet, currentRow, j, currentRow + spans[i][j][0] - 1, j + spans[i][j][1] - 1);
                }
            }
            currentRow++;
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
     * 获取Excel Sheet名称
     *
     * @return
     */
    public String getSheetName() {

        return sheetName;
    }
}
