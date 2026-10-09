package com.aptech.s09.service;

import com.aptech.s09.events.EnrolmentEvent;
import com.aptech.s09.events.EnrolmentListener;
import com.aptech.s09.model.Course;
import com.aptech.s09.model.EnrolmentReceipt;
import com.aptech.s09.model.Student;
import com.aptech.s09.policy.EnrolmentRequest;
import com.aptech.s09.policy.FeePolicy;
import com.aptech.s09.repository.StudentRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The registry's one business operation: enrol a student and tell everyone.
 *
 * <p>This class is where three of this session's patterns meet, and it is worth seeing how
 * little code that takes:
 * <ul>
 *   <li><strong>Repository</strong> — it stores through {@link StudentRepository} and never
 *       learns which store it was given.</li>
 *   <li><strong>Strategy</strong> — it asks the {@link FeePolicy} for the fee. The
 *       {@code if} that would have picked between three fee rules is not here.</li>
 *   <li><strong>Observer</strong> — it walks its listeners and calls them. It does not know
 *       what any of them are; it does not import {@code AuditLog} or {@code EmailStub}.</li>
 * </ul>
 *
 * <p>What it deliberately does <em>not</em> do is print. Every method returns something, and
 * showing it is somebody else's job — which is what makes Part 7's second view possible.
 */
public final class EnrolmentService {

    private final StudentRepository repository;
    private final List<EnrolmentListener> listeners = new ArrayList<>();
    private FeePolicy policy;

    public EnrolmentService(StudentRepository repository, FeePolicy policy) {
        this.repository = repository;
        this.policy = policy;
    }

    // ------------------------------------------------------------- the model side

    public StudentRepository repository() {
        return repository;
    }

    public List<Student> students() {
        return repository.findAll();
    }

    public Optional<Student> student(int id) {
        return repository.findById(id);
    }

    // ------------------------------------------------------------- the policy side

    public FeePolicy policy() {
        return policy;
    }

    /** Swaps the fee rule. Every caller of {@link #enrol} keeps working, unchanged. */
    public void setPolicy(FeePolicy policy) {
        this.policy = policy;
    }

    /** What this enrolment <em>would</em> cost, without enrolling anyone. */
    public BigDecimal quote(Course course, Student student, LocalDate date, int instalments) {
        return policy.feeFor(new EnrolmentRequest(student, course, date, instalments));
    }

    // ------------------------------------------------------------- the observer side

    public void addListener(EnrolmentListener listener) {
        listeners.add(listener);
    }

    public boolean removeListener(EnrolmentListener listener) {
        return listeners.remove(listener);
    }

    /** The current listeners, in the order they are notified. */
    public List<EnrolmentListener> listeners() {
        return List.copyOf(listeners);
    }

    // ------------------------------------------------------------- the operation

    /**
     * Saves the student, works out the fee with the current policy, and notifies every
     * listener. Returns the receipt so a caller can show it.
     */
    public EnrolmentReceipt enrol(Student student, Course course, LocalDate date, int instalments) {
        Student saved = repository.save(student);
        EnrolmentRequest request = new EnrolmentRequest(saved, course, date, instalments);
        BigDecimal fee = policy.feeFor(request);

        EnrolmentEvent event = new EnrolmentEvent(saved, course, fee, policy.name(), date, instalments);
        for (EnrolmentListener listener : listeners) {
            listener.onEnrolment(event);
        }

        return new EnrolmentReceipt(saved, course, fee, policy.name(), instalments, listeners.size());
    }

    /** The one-payment form of {@link #enrol}. */
    public EnrolmentReceipt enrol(Student student, Course course, LocalDate date) {
        return enrol(student, course, date, 1);
    }
}
