class RemoveFromEmergencyAction implements Action {
    private String patientId;
    private HospitalManagementSystem hms;
    
    RemoveFromEmergencyAction(String id, HospitalManagementSystem hms) { 
        this.patientId = id; 
        this.hms = hms;
    }
    
    @Override
    public void undo() {
         hms.emergencyQueue.remove(patientId);
         hms.refreshQueueTable();
    }
}