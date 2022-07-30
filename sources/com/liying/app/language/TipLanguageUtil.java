package com.liying.app.language;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TipLanguageUtil {
    private static Map<String, Map<String, String>> tipLanguageMap = new HashMap();

    static {
        Map<String, String> enMap = new HashMap<>();
        enMap.put("下载失败，请稍候再试", "Download failed. Please try again later");
        enMap.put("更新成功", "Update complete");
        enMap.put("提示", "Tips");
        enMap.put("更新包已下载完成，请重启完成更新", "The update package has been downloaded. Please restart to");
        enMap.put("升级", "Upgrade");
        enMap.put("取消", "Cancel");
        enMap.put("检测到新版本，是否更新程序？", "A new version has been detected. Please use it after updating the application. ");
        enMap.put("更新", "Update");
        enMap.put("退出", "Quit");
        tipLanguageMap.put("en", enMap);
        Map<String, String> map = new HashMap<>();
        map.put("下载失败，请稍候再试", "Der Download ist fehlgeschlagen. Bitte versuchen Sie es später noch einmal");
        map.put("更新成功", "Aktualisierung abgeschlossen");
        map.put("提示", "Tipps");
        map.put("更新包已下载完成，请重启完成更新", "Das Update-Paket wurde heruntergeladen. Bitte starten Sie neu, um das Update abzuschließen");
        map.put("升级", "Upgrade");
        map.put("取消", "Abbrechen");
        map.put("检测到新版本，是否更新程序？", "Eine neue Version wurde erkannt. Bitte verwenden Sie es nach der Aktualisierung der Anwendung.");
        map.put("更新", "Aktualisieren");
        map.put("退出", "Verlassen");
        tipLanguageMap.put("de", map);
        Map<String, String> map2 = new HashMap<>();
        map2.put("下载失败，请稍候再试", "La descarga falló, por favor intente de nuevo más tarde");
        map2.put("更新成功", "Actualización completada");
        map2.put("提示", "Consejos");
        map2.put("更新包已下载完成，请重启完成更新", "Se ha descargado el paquete de actualización, reinicie para completar la actualización");
        map2.put("升级", "Actualización");
        map2.put("取消", "Cancelar");
        map2.put("检测到新版本，是否更新程序？", "Se ha detectado una nueva versión. Por favor, úselo después de actualizar la aplicación.");
        map2.put("更新", "Actualizaciones");
        map2.put("退出", "Renunciar");
        tipLanguageMap.put("es", map2);
        Map<String, String> map3 = new HashMap<>();
        map3.put("下载失败，请稍候再试", "下載失敗，請稍候再試");
        map3.put("更新成功", "更新成功");
        map3.put("提示", "提示");
        map3.put("更新包已下载完成，请重启完成更新", "更新包已下載完成，請重啓完成更新");
        map3.put("升级", "陞級");
        map3.put("取消", "取消");
        map3.put("检测到新版本，是否更新程序？", "檢測到新版本，請更新程式後使用.");
        map3.put("更新", "更新");
        map3.put("退出", "退出");
        tipLanguageMap.put("zh", map3);
        Map<String, String> map4 = new HashMap<>();
        map4.put("下载失败，请稍候再试", "डाउनलोड विफल रहा, कृपया बाद में पुनः प्रयास करें");
        map4.put("更新成功", "अद्यतन पूर्ण");
        map4.put("提示", "टिप्स");
        map4.put("更新包已下载完成，请重启完成更新", "अपडेट पैकेज डाउनलोड किया गया है, कृपया पूर्ण अपडेट को पुनरारंभ करें");
        map4.put("升级", "अपग्रेड करें");
        map4.put("取消", "रद्द करें");
        map4.put("检测到新版本，是否更新程序？", "नए संस्करण का पता चला है, कृपया आवेदन को अपडेट करने के बाद उपयोग करें.");
        map4.put("更新", "अपडेट करें");
        map4.put("退出", "छोड़ना");
        tipLanguageMap.put("hi", map4);
        Map<String, String> map5 = new HashMap<>();
        map5.put("下载失败，请稍候再试", "다운로드에 실패했습니다. 나중에 다시 시도하십시오");
        map5.put("更新成功", "업데이트 완료");
        map5.put("提示", "힌트");
        map5.put("更新包已下载完成，请重启完成更新", "업데이트 패키지가 다운로드되었습니다. 다시 시작하여 업데이트를 완료하십시오");
        map5.put("升级", "업그레이드ं");
        map5.put("取消", "취소");
        map5.put("检测到新版本，是否更新程序？", "새 버전이 감지되었습니다. 업데이트 적용 후 사용하십시오");
        map5.put("更新", "업데이트");
        map5.put("退出", "퇴출");
        tipLanguageMap.put("ko", map5);
        Map<String, String> map6 = new HashMap<>();
        map6.put("下载失败，请稍候再试", "O download falhou, tente novamente mais tarde");
        map6.put("更新成功", "Atualização concluída");
        map6.put("提示", "Dicas");
        map6.put("更新包已下载完成，请重启完成更新", "O download do pacote de atualização foi concluído. reinicie para concluir a atualização");
        map6.put("升级", "Atualização");
        map6.put("取消", "Cancelar");
        map6.put("检测到新版本，是否更新程序？", "Se ha detectado una nueva versión. Por favor, úselo después de actualizar la aplicación.");
        map6.put("更新", "Atualizar");
        map6.put("退出", "Renunciar");
        tipLanguageMap.put("pt", map6);
        Map<String, String> map7 = new HashMap<>();
        map7.put("下载失败，请稍候再试", "Tải xuống thất bại, xin vui lòng thử lại sau");
        map7.put("更新成功", "Cập nhật xong rồi");
        map7.put("提示", "Gợi ý");
        map7.put("更新包已下载完成，请重启完成更新", "Gói cập nhật đã tải xong, vui lòng khởi động lại để hoàn thành cập nhật");
        map7.put("升级", "Nâng cấp");
        map7.put("取消", "Hủy bỏ");
        map7.put("检测到新版本，是否更新程序？", "Một phiên bản mới đã được phát hiện. Vui lòng sử dụng nó sau khi cập nhật ứng dụng.");
        map7.put("更新", "Cập nhật");
        map7.put("退出", "Bỏ");
        tipLanguageMap.put("vi", map7);
    }

    /* JADX WARNING: Removed duplicated region for block: B:42:0x007d  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x008c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String getTip(java.lang.String r4) {
        /*
            java.lang.String r0 = getLanguage()
            int r1 = r0.hashCode()
            r2 = 3201(0xc81, float:4.486E-42)
            java.lang.String r3 = "en"
            if (r1 == r2) goto L_0x006f
            r2 = 3241(0xca9, float:4.542E-42)
            if (r1 == r2) goto L_0x0067
            r2 = 3246(0xcae, float:4.549E-42)
            if (r1 == r2) goto L_0x005d
            r2 = 3329(0xd01, float:4.665E-42)
            if (r1 == r2) goto L_0x0053
            r2 = 3428(0xd64, float:4.804E-42)
            if (r1 == r2) goto L_0x0049
            r2 = 3588(0xe04, float:5.028E-42)
            if (r1 == r2) goto L_0x003f
            r2 = 3763(0xeb3, float:5.273E-42)
            if (r1 == r2) goto L_0x0035
            r2 = 3886(0xf2e, float:5.445E-42)
            if (r1 == r2) goto L_0x002b
        L_0x002a:
            goto L_0x0079
        L_0x002b:
            java.lang.String r1 = "zh"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x002a
            r0 = 3
            goto L_0x007a
        L_0x0035:
            java.lang.String r1 = "vi"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x002a
            r0 = 7
            goto L_0x007a
        L_0x003f:
            java.lang.String r1 = "pt"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x002a
            r0 = 6
            goto L_0x007a
        L_0x0049:
            java.lang.String r1 = "ko"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x002a
            r0 = 5
            goto L_0x007a
        L_0x0053:
            java.lang.String r1 = "hi"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x002a
            r0 = 4
            goto L_0x007a
        L_0x005d:
            java.lang.String r1 = "es"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x002a
            r0 = 2
            goto L_0x007a
        L_0x0067:
            boolean r0 = r0.equals(r3)
            if (r0 == 0) goto L_0x002a
            r0 = 0
            goto L_0x007a
        L_0x006f:
            java.lang.String r1 = "de"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x002a
            r0 = 1
            goto L_0x007a
        L_0x0079:
            r0 = -1
        L_0x007a:
            switch(r0) {
                case 0: goto L_0x008c;
                case 1: goto L_0x008c;
                case 2: goto L_0x008c;
                case 3: goto L_0x008c;
                case 4: goto L_0x008c;
                case 5: goto L_0x008c;
                case 6: goto L_0x008c;
                case 7: goto L_0x008c;
                default: goto L_0x007d;
            }
        L_0x007d:
            java.util.Map<java.lang.String, java.util.Map<java.lang.String, java.lang.String>> r0 = tipLanguageMap
            java.lang.Object r0 = r0.get(r3)
            java.util.Map r0 = (java.util.Map) r0
            java.lang.Object r0 = r0.get(r4)
            java.lang.String r0 = (java.lang.String) r0
            return r0
        L_0x008c:
            java.util.Map<java.lang.String, java.util.Map<java.lang.String, java.lang.String>> r0 = tipLanguageMap
            java.lang.String r1 = getLanguage()
            java.lang.Object r0 = r0.get(r1)
            java.util.Map r0 = (java.util.Map) r0
            java.lang.Object r0 = r0.get(r4)
            java.lang.String r0 = (java.lang.String) r0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.liying.app.language.TipLanguageUtil.getTip(java.lang.String):java.lang.String");
    }

    private static String getLanguage() {
        return Locale.getDefault().getLanguage();
    }
}
