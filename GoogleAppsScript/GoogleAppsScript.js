/**
 * كود البرمجة الخاص بـ Google Apps Script لربط تطبيق أندرويد بجداول بيانات جوجل
 */
const SPREADSHEET_ID = "1ODPvM4fthplHoMwyC1R1DhhDDmPLBvQ8XjZ3z5P-23g";

function doGet(e) {
  try {
    const ss = SpreadsheetApp.openById(SPREADSHEET_ID);
    const action = e.parameter.action;
    
    if (!action || action === "view") {
      return HtmlService.createHtmlOutputFromFile("Index")
        .setTitle("لوحة التحكم - إدارة الخدمات والتقييمات")
        .setXFrameOptionsMode(HtmlService.XFrameOptionsMode.ALLOWALL);
    }

    const data = {};
    data.settings = getSheetDataAsJson(ss, "Settings");
    data.services = getSheetDataAsJson(ss, "Services");
    data.portfolio = getSheetDataAsJson(ss, "Portfolio");
    data.reviews = getSheetDataAsJson(ss, "Reviews");

    return ContentService.createTextOutput(JSON.stringify({ success: true, data: data }))
      .setMimeType(ContentService.MimeType.JSON);
  } catch (error) {
    return ContentService.createTextOutput(JSON.stringify({ success: false, error: error.toString() }))
      .setMimeType(ContentService.MimeType.JSON);
  }
}

function doPost(e) {
  try {
    const ss = SpreadsheetApp.openById(SPREADSHEET_ID);
    const postData = JSON.parse(e.postData.contents);
    const action = postData.action;

    if (action === "addReview") {
      const sheet = ss.getSheetByName("Reviews");
      sheet.appendRow([postData.name, postData.comment, "Pending", new Date()]);
      return createJsonResponse(true, "تم تسجيل تعليقك بنجاح بانتظار الموافقة");
    }

    if (action === "updateReviewStatus") {
      const sheet = ss.getSheetByName("Reviews");
      sheet.getRange(postData.rowIndex, 3).setValue(postData.status);
      return createJsonResponse(true, "تم التحديث");
    }

    return createJsonResponse(false, "إجراء غير معروف");
  } catch (error) {
    return createJsonResponse(false, error.toString());
  }
}

function getSheetDataAsJson(ss, sheetName) {
  const sheet = ss.getSheetByName(sheetName);
  if (!sheet) return [];
  const data = sheet.getDataRange().getValues();
  if (data.length < 2) return [];
  const headers = data[0].map(h => String(h).trim().toLowerCase().replace(/\s+/g, '_'));
  const results = [];
  for (let i = 1; i < data.length; i++) {
    const row = data[i];
    const rowObj = { rowIndex: i + 1 };
    for (let j = 0; j < headers.length; j++) {
      rowObj[headers[j]] = row[j];
    }
    results.push(rowObj);
  }
  return results;
}

function createJsonResponse(success, message) {
  return ContentService.createTextOutput(JSON.stringify({ success: success, message: message }))
    .setMimeType(ContentService.MimeType.JSON);
}