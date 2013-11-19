package com.hqb360.common.excel;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 功能描述： 创建Excel接口
 *
 * @author: Zhenbin.Li
 * email： zhenbin.li@okhqb.com
 * company：华强北在线
 * Date: 13-11-19 Time：下午12:42
 */
public interface ExportExcelAware<T> {

    /**
     * 创建生成Excel文件
     *
     * <p>需要实现接口中的每一个方法， 接口中的方法都对应生成Excel的一个功能</p>     *
     * @param response HttpServletResponse
     * @param exportList 输出到Excel表格list
     */
    public void create(HttpServletResponse response, List<T> exportList);

    /**
     * 初始化Excel报表标题
     *
     * @return 报表标题
     */
    public String initTitle();

    /**
     * 初始化Excel文件名称
     *
     * @return
     */
    public String initFileName();

    /**
     * 初始化Excel Sheet名称
     *
     * @return
     */
    public String initSheetName();

    /**
     * 获得报表列头标题
     *
     * 1、如果是一行列头标题，则可以通过如下代码实现：
     * 		String[][] colTitles = { { "列1", "列2", "列3" } }
     * 		return colTitles;
     * 2、如果是二行列头标题，则可以通过如下代码实现：
     * 		String[][] colTitles = { { "行1列1", "行1列2", null, "行1列4" },
     * 								 { null, "行2列2", "行2列3", "行2列4" }};
     * 		return colTitles;
     * 	  将显示的列标题实际效果为：
     * 	   <table border="1">
     * 			<tr align="center">
     * 				<td rowspan="2">行1列1</td>
     * 				<td colspan="2">行1列2</td>
     * 				<td>行1列4</td>
     * 			</tr>
     * 			<tr align="center">
     * 				<td>行2列2</td>
     * 				<td>行2列3</td>
     * 				<td>行2列4</td>
     * 			</tr>
     * 		</table>
     * @return 列头标题
     */
    public String[][] initColTitles();

    /**
     * 处理Excel报表展示的数据
     *
     * @param exportList 输出到Excel list集合
     * @return 1、列表，List(String[])；
     *         2、有合并一列的数据，Map(List(String[]))；
     *         3、有合并两列的数据，Map(Map(List(String[])))。
     */
    public Object handleBodyData(List<T> exportList);

    /**
     * 初始化Excel报表展示的样式
     *
     * @return 列表，Map(int,String )；
     */
    public Object initRowStyle();

    /**
     * 初始化Excel列宽
     *
     * @return 列宽
     */
    public int[] initColumnWidth();

    /**
     * 初始化Excel报表备注信息
     *
     * @return 报表备注信息
     */
    public String[] initRemark();

}

