import java.util.*;

public class GradeManager {
    private Map<Student, List<CourseGrade>> records = new HashMap<>();

    public void addStudent(Student student) {
        if (!records.containsKey(student)) {
            records.put(student, new ArrayList<>());
        }
    }

    public void assignGrade(Student student, Course course, String grade) {
        List<CourseGrade> grades = records.get(student);
        if (grades != null) {
            grades.add(new CourseGrade(course, grade));
        }
    }

    public double calculateGPA(Student student) {
        List<CourseGrade> grades = records.get(student);
        if (grades == null || grades.isEmpty()) return 0.0;

        double totalPoints = 0;
        int totalCredits = 0;

        for (CourseGrade cg : grades) {
            totalPoints += cg.getGradePoint() * cg.getCourse().getCredits();
            totalCredits += cg.getCourse().getCredits();
        }
        return totalCredits == 0 ? 0.0 : totalPoints / totalCredits;
    }

    public void printReport(Student student) {
        List<CourseGrade> grades = records.get(student);
        if (grades == null) {
            System.out.println("No records for student " + student.getName());
            return;
        }

        System.out.println("Report for " + student.getName() + " (Email: " + student.getEmail() + ")");
        for (CourseGrade cg : grades) {
            System.out.println("- " + cg.getCourse().getCourseName() + ": Grade = " + cg.getGrade());
        }
        System.out.printf("GPA: %.2f%n", calculateGPA(student));
        System.out.println("====================================");
    }


    public Set<Student> getAllStudents() {
        return records.keySet();
    }
}
