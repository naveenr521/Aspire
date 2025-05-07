import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        GradeManager manager = new GradeManager();

        while (true) {
            System.out.println("\nEnter student details:");
            System.out.print("ID: ");
            int id = sc.nextInt();
            sc.nextLine();
            System.out.print("Name: ");
            String name = sc.nextLine();
            System.out.print("Email: ");
            String email = sc.nextLine();

            Student s = new Student(id, name, email);
            manager.addStudent(s);

            System.out.print("How many courses for " + name + "? ");
            int numCourses = sc.nextInt();
            sc.nextLine();

            for (int j = 0; j < numCourses; j++) {
                System.out.println("Course #" + (j + 1));
                System.out.print("Course ID: ");
                int courseId = sc.nextInt();
                sc.nextLine();
                System.out.print("Course Name: ");
                String courseName = sc.nextLine();
                System.out.print("Credits: ");
                int credits = sc.nextInt();
                sc.nextLine();
                System.out.print("Grade (A/B/C/D/F): ");
                String grade = sc.nextLine();

                Course c = new Course(courseId, courseName, credits);
                manager.assignGrade(s, c, grade);
            }


            System.out.print("Do you want to enter another student? (yes/no): ");
            String choice = sc.nextLine().toLowerCase();
            if (!choice.equals("yes")) {
                break;
            }
        }

        // Print all student reports
        System.out.println("\n=== Student Academic Reports ===");
        for (Student s : manager.getAllStudents()) {
            manager.printReport(s);
        }

        sc.close();
    }
}
