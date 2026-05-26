package com.animalsalvation.storage;

import com.animalsalvation.entity.AdoptionApplication;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * 领养申请列表文件存储类。
 *
 * <p>用于保存待审核和已审核领养申请。</p>
 */
public final class AdoptionApplicationFileStorage {
    private AdoptionApplicationFileStorage() {
    }

    /** 保存领养申请列表。 */
    public static void save(List<AdoptionApplication> applications, Path file) throws IOException {
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"applyId", "animalId", "applicantName", "phone", "reason", "status", "applyTime"});
        for (AdoptionApplication application : applications) {
            rows.add(new String[]{
                    String.valueOf(application.getApplyId()),
                    String.valueOf(application.getAnimalId()),
                    application.getApplicantName(),
                    application.getPhone(),
                    application.getReason(),
                    application.getStatus(),
                    CsvFiles.formatTime(application.getApplyTime())
            });
        }
        CsvFiles.writeRows(file, rows);
    }

    /** 读取领养申请列表。 */
    public static List<AdoptionApplication> load(Path file) throws IOException {
        List<AdoptionApplication> applications = new ArrayList<>();
        List<String[]> rows = CsvFiles.readRows(file);
        for (int i = 1; i < rows.size(); i++) {
            String[] row = rows.get(i);
            applications.add(new AdoptionApplication(
                    Integer.parseInt(row[0]),
                    Integer.parseInt(row[1]),
                    row[2],
                    row[3],
                    row[4],
                    row[5],
                    CsvFiles.parseTime(row[6])
            ));
        }
        return applications;
    }
}
