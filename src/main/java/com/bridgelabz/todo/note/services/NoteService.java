package com.bridgelabz.todo.note.services;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;

import org.springframework.web.multipart.MultipartFile;

import com.bridgelabz.todo.note.exceptions.CollaborationException;
import com.bridgelabz.todo.note.exceptions.ImageDeletionException;
import com.bridgelabz.todo.note.exceptions.LabelNotFoundException;
import com.bridgelabz.todo.note.exceptions.NoteIdRequredException;
import com.bridgelabz.todo.note.models.CreateNoteDto;
import com.bridgelabz.todo.note.models.NoteDto;
import com.bridgelabz.todo.note.models.UpdateNoteDto;
import com.bridgelabz.todo.user.exceptions.UserNotFoundException;
import com.bridgelabz.todo.user.models.UserDto;

public interface NoteService {

	NoteDto createNote(CreateNoteDto noteDto, long userId, String origin) throws LabelNotFoundException, IOException, MessagingException;

	void updateNote(UpdateNoteDto noteDto, long userId);

	void deleteNote(long noteId, long userId);

	List<NoteDto> getAllNotes(long userId);

	void changePinStatus(long noteId, boolean status, long userId);

	void changeArchiveStatus(long noteId, boolean status, long userId);

	void changeTrashStatus(long noteId, boolean status, long userId);

	String saveImage(MultipartFile image);
	
	void addReminder(long noteId, long time, long userId);

	void removeReminnder(long noteId, long userId);

	void deleteImage(String imagename) throws NoteIdRequredException, ImageDeletionException;

	void changeColor(long noteId, String color, long userId);

	String saveImageToNote(MultipartFile image, long id, String url, long userId);

	UserDto collaborate(long noteId, String emailId, long userId, String origin)
			throws UserNotFoundException, CollaborationException, MessagingException, IOException;

	void removeCollaborator(long noteId, long parseLong, long collaboratorId);

	void deleteImage(String imagename, long id, long userId) throws NoteIdRequredException, ImageDeletionException;
}
