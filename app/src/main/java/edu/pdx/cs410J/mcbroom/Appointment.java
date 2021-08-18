package edu.pdx.cs410J.mcbroom;

import edu.pdx.cs410J.AbstractAppointment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Appointment extends AbstractAppointment implements Comparable<Appointment> {

  static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
  private String description;
  private Date begin, end;


  public Appointment(String description, Date begin, Date end) {
    this.description = description;
    this.begin = begin;
    this.end = end;
  }

  @Override
  public String getDescription() { return this.description; }

  public Date getBeginTime() { return this.begin; }

  public Date getEndTime() { return this.end; }

  @Override
  public String getBeginTimeString() {
    return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(begin);
  }

  @Override
  public String getEndTimeString() {
    return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(end);
  }

  @Override
  public int compareTo(Appointment appointment) {
    if(this.begin.compareTo(appointment.begin) < 0) return -1;

    if(this.begin.compareTo(appointment.begin) > 0) return 1;

    if(this.end.compareTo(appointment.end) < 0) return -1;

    if(this.end.compareTo(appointment.end) > 0) return 1;

    if(this.description.compareTo(appointment.description) < 0) return -1;

    if(this.description.compareTo(appointment.description) > 0) return 1;

    return 0;
  }
}