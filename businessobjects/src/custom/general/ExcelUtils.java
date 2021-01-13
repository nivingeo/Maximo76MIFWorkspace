package custom.general;

import java.io.IOException;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFName;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;

public class ExcelUtils {

	public static final String DATA_TYPE_INTEGER = "integer";
	public static final String DATA_TYPE_FLOAT = "float";
	public static final String DATA_TYPE_TEXT = "text";
	
	/**
	 * Map containing pair [comboName,column_values_index], for example [lineType,1], [craftSkill,2]
	 */
	private Map<String, Integer> codeIndexes;
	
	private MXLogger logger = MXLoggerFactory.getLogger("maximo.service");

	public ExcelUtils(){
		this.codeIndexes = new HashMap<String, Integer>();
	}


	/**
	 * Creates sheet header
	 * @param workbook
	 * @param sheet
	 * @param headerValues
	 */
	public void createHeader(HSSFWorkbook workbook, HSSFSheet sheet, String[] headerValues){

		logger.debug("------creating sheet header for sheet: " + sheet.getSheetName());
		
		HSSFCellStyle cs = workbook.createCellStyle();
		HSSFRow row = sheet.createRow(0);
		HSSFCell cell = null;

		cs.setFillBackgroundColor(HSSFColor.BRIGHT_GREEN.index);
		cs.setAlignment(CellStyle.ALIGN_CENTER);
		cs.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));
		for (int i = 0; i <headerValues.length ; i++) {
			cell = row.createCell(i);
			cell.setCellValue(headerValues[i]);
			sheet.setColumnWidth(i, 7500);
			sheet.setDefaultColumnStyle(i, cs);
		}
	}

	/**
	 * Creates combo box on provided sheet with provided values. Values will be placed
	 * on separate sheet (codeSheet)
	 * @param sheet
	 * @param codeSheet
	 * @param optionsName
	 * @param mboSetRemote 
	 * @param mboAttributeName name of MBO attribute whose values will be in combo box
	 * @param sheetColumnIndex
	 * @throws MXException
	 * @throws RemoteException
	 */
	public void createComboBox(HSSFSheet sheet, HSSFSheet codeSheet,MboSetRemote mboSetRemote,   
		String mboAttributeName, int sheetColumnIndex) throws MXException, RemoteException{
		
		List<String> values = new ArrayList<String>();
		if ( mboSetRemote != null ){
			for (int i=0; i<mboSetRemote.count(); i++){
				MboRemote mboRemote = mboSetRemote.getMbo(i);
				values.add(mboRemote.getString(mboAttributeName));
			}
		}
		String[] stringArray = {};
		stringArray = values.toArray(stringArray);
		
		createComboBox(sheet, codeSheet, sheetColumnIndex, stringArray);
	}

	/**
	 * Creates combo box on provided sheet with provided values. Values will be placed
	 * on separate sheet (codeSheet)
	 * @param sheet
	 * @param codeSheet
	 * @param optionsName 
	 * @param sheetColumnIndex
	 * @param comboValues
	 */
	public void createComboBox(HSSFSheet sheet, HSSFSheet codeSheet, 
			int sheetColumnIndex, String[] comboValues){
		
		HSSFWorkbook workbook = sheet.getWorkbook();
		int codeColIndex = codeIndexes.size();
		String optionsName = "option_" + String.valueOf(codeColIndex);
		codeIndexes.put(optionsName, codeColIndex);
		
		logger.debug("------createComboBox, optionsName: " + optionsName + ", codeColIndex: " + codeColIndex);

		HSSFCellStyle csBody = getTextCellStyle(workbook);
		
		for (int i = 0; i<comboValues.length; i++) {
			HSSFRow row = codeSheet.getRow(i+1);
			if ( row == null )
				row = codeSheet.createRow(i+1);
			HSSFCell cell = row.createCell(codeColIndex);
			cell.setCellValue(comboValues[i]);
			cell.setCellStyle(csBody);
		}

		HSSFName namedRange = workbook.createName();
		namedRange.setNameName(optionsName);

		String columnName = CellReference.convertNumToColString(codeColIndex);
		String codesSheetName = codeSheet.getSheetName();
		
		String formulaRegion = "'" + codesSheetName + "'!$" + columnName +"$2:$" + columnName + "$" + String.valueOf(comboValues.length + 1);
		logger.debug("------formulaRegion: " + formulaRegion);

		namedRange.setRefersToFormula(formulaRegion);
		HSSFDataValidation unitsOfMeasureValidation = new HSSFDataValidation(
				new CellRangeAddressList(1, 1000, sheetColumnIndex, sheetColumnIndex), DVConstraint.createFormulaListConstraint(optionsName));

		unitsOfMeasureValidation.setSuppressDropDownArrow(false);
		sheet.addValidationData(unitsOfMeasureValidation);
	}
	
	/**
	 * returns format for text cell
	 * @param workbook
	 * @return
	 */
	public HSSFCellStyle getTextCellStyle(HSSFWorkbook workbook){
		HSSFCellStyle csBody = workbook.createCellStyle();
		csBody.setFillBackgroundColor(HSSFColor.WHITE.index);
		csBody.setAlignment(CellStyle.ALIGN_CENTER);
		csBody.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));
		csBody.setWrapText(true);
		
		return csBody;
	}
	
	/**
	 * returns format for integer cell
	 * @param workbook
	 * @return
	 */
	public HSSFCellStyle getIntegerCellStyle(HSSFWorkbook workbook){
		HSSFCellStyle csWhole = workbook.createCellStyle();
		csWhole.setAlignment(CellStyle.ALIGN_RIGHT);
		csWhole.setDataFormat(HSSFDataFormat.getBuiltinFormat("0"));
		
		return csWhole;
	}
	
	/**
	 * returns format for float cell
	 * @param workbook
	 * @return
	 */	
	public HSSFCellStyle getFloatCellStyle(HSSFWorkbook workbook){
		HSSFCellStyle csNum = workbook.createCellStyle();
		DataFormat format = workbook.createDataFormat();
		csNum.setAlignment(CellStyle.ALIGN_RIGHT);
		csNum.setDataFormat(format.getFormat("#,##0.000"));
		
		return csNum;
	}	
	
	public void createStyleColumn(HSSFSheet sheet, int columnIndex, String dataType){
		
		HSSFWorkbook workbook = sheet.getWorkbook();
		
		HSSFCellStyle style = null;
		if ( DATA_TYPE_INTEGER.equals(dataType) )
			style = getIntegerCellStyle(workbook);
		else if ( DATA_TYPE_FLOAT.equals(dataType) )
			style = getFloatCellStyle(workbook);
		else
			style = getTextCellStyle(workbook);
		
		sheet.setDefaultColumnStyle(columnIndex, style);
	}

	public void streamExcelToResponse(HSSFWorkbook workbook, String filename, HttpServletResponse response)
    	throws IOException {
		//logger.debug("Streaming Excel file to respone. Filename: " + filename);
		response.setContentType("application/vnd.ms-excel; charset=UTF-8");
		response.setHeader("Pragma", "public");
		response.setHeader("Cache-Control", "max-age=0");
		response.setHeader("Content-Disposition", "inline; filename=" + filename);
		OutputStream outStream = response.getOutputStream();
		workbook.write(outStream);
		outStream.close();
		response.flushBuffer();
	}

	public int[] validateRequiredCells(HSSFRow row, int[] indexes){
		// indexes = {3,5,6,9};
		List<Integer> emptyCells = new ArrayList<Integer>();
		
		for ( int i=0; i<indexes.length; i++ ){
			int index = indexes[i];
			HSSFCell cell = row.getCell(index);
			if ( cell == null )
				emptyCells.add(index);
		}
		
		int[] emptyIndexes = new int[emptyCells.size()];
		
		for ( int i=0; i<emptyCells.size(); i++ )
			emptyIndexes[i] = emptyCells.get(i).intValue();
		
		return emptyIndexes;
		
	}
	
}
