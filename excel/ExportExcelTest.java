package com.hqb360.common.excel;

import com.google.common.collect.Lists;
import com.hqb360.common.lang.CollectionUtil;
import com.hqb360.common.lang.NumberFormatUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 功能描述：
 *
 * @author: Zhenbin.Li
 * email： zhenbin.li@okhqb.com
 * company：华强北在线
 * Date: 13-11-19 Time：下午8:47
 */
public class ExportExcelTest {

    /**
     * 导出库存列表
     *
     * @param response
     * @return
     */
    @RequestMapping(value = "/invoicing/itemSkusWarehouse/exportItemSkusWarehouse.resource", method = RequestMethod.GET)
    public String exportItemSkusWarehouse(HttpServletResponse response) {

        final List<ItemSkuWarehouseExportDo> list = itemSkusWarehouseQueryBiz
                .findItemSkuWarehouseStatisticsForExport().getItemSkuWarehouseExportDos();

        new DefaultExportExcelAdapter<ItemSkuWarehouseExportDo>() {

            @Override
            public String initTitle() {
                return "库存列表";
            }

            @Override
            public String[][] initColTitles() {
                String[][] colTitles = {{"所属类目", "SKU ID", "SKU名称", "良品库", "申购库", "次品库", "赠品库", "寄存库", "成本价", "合计"}};
                return colTitles;
            }

            @Override
            public Object handleBodyData(List<ItemSkuWarehouseExportDo> exportList) {

                List<String[]> exports = Lists.newArrayList();
                if (CollectionUtil.isNotEmpty(exportList)) {
                    for (ItemSkuWarehouseExportDo itemSkuWarehouseExportDo : exportList) {
                        String[] itemSkuWarehouseExportDoArray = new String[initColTitles()[0].length];
                        itemSkuWarehouseExportDoArray[0] = itemSkuWarehouseExportDo.getBelongCatName();
                        itemSkuWarehouseExportDoArray[1] = itemSkuWarehouseExportDo.getSkuId().toString();
                        itemSkuWarehouseExportDoArray[2] = itemSkuWarehouseExportDo.getSkuName();
                        itemSkuWarehouseExportDoArray[3] = itemSkuWarehouseExportDo.getStandardWarehouse().toString();
                        itemSkuWarehouseExportDoArray[4] = itemSkuWarehouseExportDo.getApWarehouse().toString();
                        itemSkuWarehouseExportDoArray[5] = itemSkuWarehouseExportDo.getSubstandardWarehouse().toString();
                        itemSkuWarehouseExportDoArray[6] = itemSkuWarehouseExportDo.getDonationWarehouse().toString();
                        itemSkuWarehouseExportDoArray[7] = itemSkuWarehouseExportDo.getRegisterWarehouse().toString();
                        itemSkuWarehouseExportDoArray[8] = NumberFormatUtil.formatForIgnoreZero(itemSkuWarehouseExportDo.getCostPrice(), 5);
                        itemSkuWarehouseExportDoArray[9] = itemSkuWarehouseExportDo.getCountWarehouse().toString();
                        exports.add(itemSkuWarehouseExportDoArray);
                    }
                }
                return exports;
            }

            @Override
            public String initSheetName() {
                return "库存列表";
            }

            @Override
            public int[] initColumnWidth() {
                int[] columnWidths = {150, 100, 500, 0, 0, 0, 0, 0, 0, 0};
                return columnWidths;
            }

        }.create(response, list);

        return null;
    }

}
