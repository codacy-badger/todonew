package com.bridgelabz.todo.note.factories;

import java.util.Arrays;
import java.util.LinkedList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import com.bridgelabz.todo.note.models.Note;
import com.bridgelabz.todo.note.models.NoteDto;
import com.bridgelabz.todo.note.models.NoteExtras;
import com.bridgelabz.todo.note.models.CreateNoteDto;
import com.bridgelabz.todo.note.models.Label;
import com.bridgelabz.todo.note.models.LabelDto;

@Component
public class NoteFactory {

	@Autowired
	private WebApplicationContext context;

	public Note getNoteFromCreateNoteDto(CreateNoteDto noteDto) {
		Note note = context.getBean(Note.class);

		note.setTitle(noteDto.getTitle());
		note.setBody(noteDto.getBody());
		note.setImageUrls(noteDto.getImageUrls());

		NoteExtras extras = context.getBean(NoteExtras.class);

		extras.setNote(note);
		extras.setPinned(noteDto.isPinned());
		extras.setReminder(noteDto.getReminder());
		extras.setTrashed(noteDto.isTrashed());
		extras.setArchived(noteDto.isArchived());

		if (noteDto.getColor() == null) {
			extras.setColor("#fff");
		} else {
			extras.setColor(noteDto.getColor());
		}

		note.setNoteExtras(Arrays.asList(extras));

		return note;
	}

	public NoteDto getNoteDtoFromNoteAndExtras(Note note, NoteExtras extras) {
		NoteDto noteDto = context.getBean(NoteDto.class);

		noteDto.setId(note.getId());
		noteDto.setImageUrls(note.getImageUrls());
		noteDto.setTitle(note.getTitle());
		noteDto.setBody(note.getBody());
		noteDto.setCreatedAt(note.getCreatedAt());
		noteDto.setUpdatedAt(note.getUpdatedAt());

		noteDto.setArchived(extras.isArchived());
		noteDto.setColor(extras.getColor());
		noteDto.setPinned(extras.isPinned());
		noteDto.setReminder(extras.getReminder());
		noteDto.setTrashed(noteDto.isTrashed());

		noteDto.setLabels(new LinkedList<>());
		for (Label label : extras.getLabels()) {
			LabelDto dto = context.getBean(LabelDto.class);
			dto.setId(label.getId());
			dto.setName(label.getName());

			noteDto.getLabels().add(dto);
		}

		return noteDto;
	}

	public LabelDto getLabelDtoFromLabel(Label label) {
		LabelDto labelDto = context.getBean(LabelDto.class);

		labelDto.setId(label.getId());
		labelDto.setName(label.getName());

		return labelDto;
	}
}
