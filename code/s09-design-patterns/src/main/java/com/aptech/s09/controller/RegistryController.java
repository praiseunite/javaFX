package com.aptech.s09.controller;

import com.aptech.s09.events.EnrolmentListener;
import com.aptech.s09.model.Course;
import com.aptech.s09.model.Student;
import com.aptech.s09.policy.FeePolicy;
import com.aptech.s09.service.EnrolmentService;
import com.aptech.s09.view.RegistryView;

import java.time.LocalDate;
import java.util.Map;

/**
 * The "C" in MVC: it takes a request from outside, asks the model to do the work, and hands
 * the result to the view for showing.
 *
 * <p>Look at what this class does not contain: no SQL, no fee arithmetic, no format string.
 * It decides <em>in what order</em> things happen and <em>which</em> view is told. Everything
 * else belongs to somebody else.
 *
 * <p>The view is a field with a setter, not a constructor-only dependency, because "which
 * view" is a decision the program makes at run time — a console today, a window in Session
 * 15, both at once if you want it. {@link #setView} is how Example 7 shows the same model
 * through two views without recompiling anything.
 */
public final class RegistryController {

    private final EnrolmentService service;
    private final Map<Integer, Course> courses;
    private RegistryView view;

    public RegistryController(EnrolmentService service, Map<Integer, Course> courses, RegistryView view) {
        this.service = service;
        this.courses = courses;
        this.view = view;
    }

    /** The model side, for a caller that needs it directly. */
    public EnrolmentService service() {
        return service;
    }

    /** Swaps the view. The model and this controller are not touched. */
    public void setView(RegistryView view) {
        this.view = view;
    }

    public RegistryView view() {
        return view;
    }

    public void setPolicy(FeePolicy policy) {
        service.setPolicy(policy);
    }

    public void addListener(EnrolmentListener listener) {
        service.addListener(listener);
    }

    // ------------------------------------------------------------------ requests

    /** Show every student in the registry. */
    public void listStudents() {
        view.showStudents(service.students());
    }

    /** Show the students on one course, or say the course is not in the catalogue. */
    public void listStudentsOnCourse(int courseId) {
        Course course = courses.get(courseId);
        if (course == null) {
            view.showMessage("no course with id " + courseId + " in the catalogue");
            return;
        }
        view.showMessage(course.label() + ":");
        view.showStudents(service.repository().findByCourse(courseId));
    }

    /** Enrol a new student on a course and show the receipt. */
    public void enrol(String name, String email, int courseId, LocalDate date, int instalments) {
        Course course = courses.get(courseId);
        if (course == null) {
            view.showMessage("no course with id " + courseId + " in the catalogue");
            return;
        }
        Student student = Student.unsaved(name, email, courseId, null);
        view.showReceipt(service.enrol(student, course, date, instalments));
    }
}
