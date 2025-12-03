public class RemoveFromRegularAction implements Action {
    private String patientId;
    private HospitalManagementSystem hms;
    
    RemoveFromRegularAction(String id, HospitalManagementSystem hms) { 
        this.patientId = id; 
        this.hms = hms;
    }
    
    @Override
    public void undo() {
         hms.regularQueue.remove(patientId);
         hms.refreshQueueTable();
    }
}