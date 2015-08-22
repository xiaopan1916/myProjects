package com.hqb360.common.excel;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hssf.util.Region;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 功能描述：ExportExcelFactory工具类
 *
 * @author: Zhenbin.Li
 * email： zhenbin.li@okhqb.com
 * company：华强北在线
 * Date: 13-11-19 Time：下午12:48
 */
@SuppressWarnings("deprecation")
public class ExportExcelUtil {

    /**
     * 创建列表的主体数据
     *
     * @param workbook
     * @param list       List(String[])列表数据
     * @param color
     * @param sheet
     * @param currentRow
     */
    @SuppressWarnings("rawtypes")
    protected static void createBodyColor(HSSFWorkbook workbook, HSSFSheet sheet,
                                          List list, Map color, int currentRow) {

        HSSFFont font_black = getFont(workbook, 10, HSSFFont.BOLDWEIGHT_NORMAL, HSSFColor.BLACK.index);
        HSSFCellStyle style_black = getStyle(workbook, font_black, HSSFCellStyle.ALIGN_CENTER);
        HSSFFont font_red = getFont(workbook, 10, HSSFFont.BOLDWEIGHT_NORMAL, HSSFColor.RED.index);
        HSSFCellStyle style_red = getStyle(workbook, font_red, HSSFCellStyle.ALIGN_CENTER);
        HSSFFont font_green = getFont(workbook, 10, HSSFFont.BOLDWEIGHT_NORMAL, HSSFColor.GREEN.index);
        HSSFCellStyle style_green = getStyle(workbook, font_green, HSSFCellStyle.ALIGN_CENTER);

        for (int index = 0; index < list.size(); index++) {
            String rowColor = (String) color.get(index);

            HSSFRow row = sheet.createRow(currentRow);
            String[] stringArray = (String[]) list.get(index);
            for (int j = 0; j < stringArray.length; j++) {
                if (rowColor != null) {
                    if (rowColor.equals("RED")) {
                        createCell(row, j, style_red, stringArray[j]);
                    } else if (rowColor.equals("GREEN")) {
                        createCell(row, j, style_green, stringArray[j]);
                    } else {
                        createCell(row, j, style_black, stringArray[j]);
                    }
                } else {
                    createCell(row, j, style_black, stringArray[j]);
                }

            }
            currentRow++;
        }
    }

    /**
     * 创建合并一列的主体数据
     *
     * @param map        Map(List(String[])) 嵌套一层的Map数据
     * @param sheet
     * @param currentRow
     */
    @SuppressWarnings("rawtypes")
    protected static void createOneMapBody(HSSFWorkbook workbook, HSSFSheet sheet, Map map, int currentRow) {

        HSSFFont font = getFont(workbook, 10, HSSFFont.BOLDWEIGHT_NORMAL, HSSFColor.BLACK.index);
        HSSFCellStyle style = getStyle(workbook, font, HSSFCellStyle.ALIGN_CENTER);

        for (Iterator it = map.keySet().iterator(); it.hasNext(); ) {
            String key = (String) it.next();
            List list = (List) map.get(key);
            int size = list.size();
            mergeRow(sheet, currentRow, currentRow + size - 1, 0);

            for (int i = 0; i < size; i++) {
                HSSFRow row = sheet.createRow(currentRow);
                createCell(row, 0, style, key);

                String[] stringArray = (String[]) list.get(i);
                for (int j = 0; j < stringArray.length; j++) {
                    createCell(row, j + 1, style, stringArray[j]);
                }
                currentRow++;
            }
        }
    }

    /**
     * 创建合并二列的主体数据
     *
     * @param map        Map(Map(List(String[]))) 嵌套二层的Map数据
     * @param sheet
     * @param currentRow
     */
    @SuppressWarnings("rawtypes")
    protected static void createTwoMapBody(HSSFWorkbook workbook, HSSFSheet sheet, Map map, int currentRow) {

        HSSFFont font = getFont(workbook, 10, HSSFFont.BOLDWEIGHT_NORMAL, HSSFColor.BLACK.index);
        HSSFCellStyle style = getStyle(workbook, font, HSSFCellStyle.ALIGN_CENTER);

        //第一列的合并行数
        int[] rowSpans = getRowSpanOfTwoMap(map);
        int rowCount = 0;
        for (Iterator iterator = map.keySet().iterator(); iterator.hasNext(); rowCount++) {

            String key1 = (String) iterator.next();
            Map map1 = (Map) map.get(key1);

            mergeRow(sheet, currentRow, currentRow + rowSpans[rowCount] - 1, 0);

            for (Iterator it2 = map1.keySet().iterator(); it2.hasNext(); ) {
                String key2 = (String) it2.next();
                List list = (List) map1.get(key2);
                int size = list.size();
                mergeRow(sheet, currentRow, currentRow + size - 1, 1);

                for (int i = 0; i < size; i++) {
                    HSSFRow row = sheet.createRow(currentRow);
                    createCell(row, 0, style, key1);
                    createCell(row, 1, style, key2);

                    String[] stringArray = (String[]) list.get(i);
                    for (int j = 0; j < stringArray.length; j++) {
                        createCell(row, j + 2, style, stringArray[j]);
                    }
                    currentRow++;
                }
            }
        }
    }

    /**
     * 创建单元格
     *
     * @param row   行对象
     * @param index 在行中创建单元格的位置，从0开始。
     * @param style 单元格样式
     * @param value 单元格的值
     */
    protected static void createCell(HSSFRow row, int index, HSSFCellStyle style, String value) {

        HSSFCell cell = row.createCell((short) index);
        //cell.setEncoding(HSSFCell.ENCODING_UTF_16);
        cell.setCellStyle(style);
        cell.setCellValue(value);
    }

    /**
     * 合并行
     *
     * @param sheet
     * @param startRow 起始行（从0开始）
     * @param endRow   结束行（从0开始）
     * @param col      列位置（从0开始）
     */
    protected static void mergeRow(HSSFSheet sheet, int startRow, int endRow, int col) {

        sheet.addMergedRegion(new Region(startRow, (short) col, endRow, (short) col));
    }

    /**
     * 合并列
     *
     * @param sheet
     * @param startCol 起始列（从0开始）
     * @param endCol   结束列（从0开始）
     * @param row      行位置（从0开始）
     */
    protected static void mergeCol(HSSFSheet sheet, int startCol, int endCol, int row) {

        sheet.addMergedRegion(new Region(row, (short) startCol, row, (short) endCol));
    }

    /**
     * 合并区域
     *
     * @param sheet
     * @param startRow 起始行（从0开始）
     * @param startCol 起始列（从0开始）
     * @param endRow   结束行（从0开始）
     * @param endCol   结束列（从0开始）
     */
    protected static void mergeRegion(HSSFSheet sheet, int startRow, int startCol, int endRow, int endCol) {

        sheet.addMergedRegion(new Region(startRow, (short) startCol, endRow, (short) endCol));
    }

    /**
     * 关闭输出流
     *
     * @param out 输出流
     */
    protected static void closeOutputStream(OutputStream out) {

	// 只是个注释
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {

            }
        }
    }

    /**
     * 根据字体高度和字体类型获得字体对象
     *
     * @param fontHeight 字体高度
     * @param boldweight 字体类型
     * @param color      字体颜色
     * @return 字体对象
     */
    protected static HSSFFont getFont(HSSFWorkbook workbook, int fontHeight, short boldweight, short color) {

        HSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) fontHeight);
        font.setFontName("新宋体");
        font.setColor(color);
        font.setBoldweight(boldweight);
        return font;
    }

    /**
     * 根据字体和对其方式获得单元格样式
     *
     * @param font  字体
     * @param align 对其方式
     * @return 单元格样式
     */
    protected static HSSFCellStyle getStyle(HSSFWorkbook workbook, HSSFFont font, short align) {

        HSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font);
        //边框
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBottomBorderColor(HSSFColor.BLACK.index);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style.setLeftBorderColor(HSSFColor.BLACK.index);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setRightBorderColor(HSSFColor.BLACK.index);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setTopBorderColor(HSSFColor.BLACK.index);
        //对齐方式
        style.setAlignment(align);
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        return style;
    }

    /**
     * 获得报表标题在某行某列的行跨度
     *
     * @param colTitles 报表列标题
     * @param row       行
     * @param col       列
     * @return 行跨度
     */
    protected static int getRowSpan(String[][] colTitles, int row, int col) {

        int rowSpan = 1;
        if (colTitles[row][col] != null) {
            for (int i = row + 1; i < colTitles.length; i++) {
                if (colTitles[i][col] == null) {
                    rowSpan++;
                } else {
                    break;
                }
            }
        }
        return rowSpan;
    }

    /**
     * 获得报表标题在某行某列的列跨度
     *
     * @param colTitles 报表列标题
     * @param row       行
     * @param col       列
     * @return 列跨度
     */
    protected static int getColSpan(String[][] colTitles, int row, int col) {

        int colSpan = 1;
        if (colTitles[row][col] != null) {
            for (int i = col + 1; i < colTitles[row].length; i++) {
                if (colTitles[row][i] == null) {
                    if (row == 0) {
                        colSpan++;
                    } else if (colTitles[row - 1][i] == null) {
                        colSpan++;
                    } else {
                        break;
                    }
                } else {
                    break;
                }
            }
        }
        return colSpan;
    }

    /**
     * 获得Map或List数据的嵌套层次数。
     *
     * @param obj Map 或 List
     * @return 如果参数为null则返回-1；
     *         如果参数为List则返回0；
     *         如果为一层Map（Map中value为List）则返回1；
     *         如果为二层Map（Map中value为Map，再深层为List，则返回2；
     *         ......
     */
    @SuppressWarnings("rawtypes")
    protected static int getNestedCount(Object obj) {

        if (obj == null) {
            return -1;
        } else if (obj instanceof List) {
            return 0;
        } else if (obj instanceof Map) {
            Map map = (Map) obj;
            if (map.size() > 0) {
                Object o = map.get(map.keySet().iterator().next());
                return getNestedCount(o) + 1;
            } else {
                return 1;
            }
        }
        throw new IllegalArgumentException("参数不为List或Map");
    }

    /**
     * 获得两层嵌套Map的针对报表的第一列合并行情况
     *
     * @param map Map(Map(List))两层嵌套Map
     * @return 对应报表的第一列合并行情况
     */
    @SuppressWarnings({"rawtypes", "unused"})
    protected static int[] getRowSpanOfTwoMap(Map map) {

        int[] spans = new int[map.size()];
        Iterator it = map.keySet().iterator();
        for (int i = 0; it.hasNext(); i++) {
            Object key = it.next();
            Map map2 = (Map) map.get(key);
            Iterator it2 = map2.keySet().iterator();
            for (int i2 = 0; it2.hasNext(); i2++) {
                Object key2 = it2.next();
                List list = (List) map2.get(key2);
                spans[i] += list.size();
            }
        }
        return spans;
    }
}
