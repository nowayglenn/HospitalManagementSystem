import java.util.LinkedList;

class AddToQueueAction implements Action {
    private String patientId;
    private String queueType;
    private HospitalManagementSystem hms;
    
    AddToQueueAction(String id, String type, HospitalManagementSystem hms) { 
        this.patientId = id; 
        this.queueType = type; 
        this.hms = hms;
    }
    
    @Override
    public void undo() {
        // Re-add patient to the front of the queue using LinkedList's addFirst for correct FIFO undo
        if (queueType.equals("Emergency")) {
            ((LinkedList<String>) hms.emergencyQueue).addFirst(patientId);
        } else {
            ((LinkedList<String>) hms.regularQueue).addFirst(patientId);
        }
        hms.refreshQueueTable();
    }
}