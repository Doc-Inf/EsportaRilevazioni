package controller;

import java.time.LocalDateTime;
import java.util.List;

import model.Rilevazione;

public interface RilevazioniParser {
	List<Rilevazione> parseFile();
	List<Rilevazione> parseFile(LocalDateTime startDate);
}

