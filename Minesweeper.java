import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

public class Minesweeper {

	private static final int ROWS = 5; // Filas
	private static final int COLS = 5; // Columnas
	private static final int MAX_UNCOVERED = 6; // Cantidad necesaria de casillas reveladas para ganar

	private static char[][] board = new char[ROWS][COLS]; // Representación del tablero de juego.
	private static boolean[][] revealed = new boolean[ROWS][COLS]; // Matriz de celdas reveladas.
	private static boolean[][] boardMines = new boolean[ROWS][COLS]; // Matriz de celdas con minas.
	private static int uncovered = 0;

	public static void main(String[] args) {

		boolean gameover = false;

		// Obtener dificultad.
		Scanner scanner = new Scanner(System.in);
		System.out.println("Ingrese Dificultad: ");
		System.out.println("Facil(F) , Medio(M), Dificil(D)");
		String difficulty = scanner.nextLine();

		// Inicializar el tablero con la dificultad seleccionada.
		String difficultyToApply = checkDifficulty(difficulty);
		initializeBoard(difficultyToApply);
		printBoard(false);

		// Ejecución
		while (uncovered <= MAX_UNCOVERED) {
			// Obtener coordenadas
			System.out.print("Ingrese coordenada (fila columna): ");
			int row = 0;
			int col = 0;

			try {
				row = scanner.nextInt();
				col = scanner.nextInt();
			} catch (InputMismatchException e) {
				System.out.println("Coordenada no válida");
				scanner.nextLine();
				continue;
			}

			if (row < 0 || row >= ROWS || col < 0 || col >= COLS) {
				System.out.println("Coordenada no válida");
				continue;
			}

			// Si celda contiene mina, BOOM.
			if (boardMines[row][col]) {
				gameover = true;
				System.out.println("GG WP! Game over!");
				break;
			}

			// Revelar celda y continuar
			uncoverCell(row, col);
			printBoard(false);
		}

		if (!gameover) {
			System.out.println("FELICIDADES!! GANASTE!!");
		}
		// Si perdió o descubrió las celdas necesarias, mostrar tablero con posición de
		// las minas.
		printBoard(true);

	}

	/**
	 * Método que en base a la dificultad ingresada por el usuario, retorna el
	 * código de la dificultad. Si se ingresa un código erroneo, deja la dificultad
	 * FÁCIL por defecto. Si no se ingresa dificultad, asigna dificultad FÁCIL por
	 * defecto.
	 * 
	 * @param difficulty Dificultad ingresada por el usuario
	 * @return Código de la dificultad asociada.
	 */
	private static String checkDifficulty(String difficulty) {

		if (difficulty.isBlank()) {
			System.out.println("Se aplicará dificultad Fácil por defecto");
			return "F";
		}

		String newDifficulty = difficulty.toUpperCase().substring(0, 1);
		if (newDifficulty.equals("F") || newDifficulty.equals("M") || newDifficulty.equals("D")) {
			return newDifficulty;
		}

		System.out.println("Código de dificultad incorrecto. Se aplicará dificultad Fácil por defecto ...");
		return "F";

	}

	/**
	 * Inicializa el tablero de juego. Recibe como parametro el código de la
	 * dificultad para asignar la cantidad correspondiente de minas al tablero.
	 * 
	 * @param difficultyToApply Código de dificultad.
	 */
	private static void initializeBoard(String difficultyToApply) {

		int minesNumber = 0;
		if (difficultyToApply.equals("F")) {
			minesNumber = 10;
		} else if (difficultyToApply.equals("M")) {
			minesNumber = 12;
		} else if (difficultyToApply.equals("D")) {
			minesNumber = 14;
		}

		System.out.println("Debes evitar " + minesNumber + " minas. Buena Suerte!");
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS; j++) {
				board[i][j] = '*';
				revealed[i][j] = false;
				boardMines[i][j] = false;
			}
		}

		placeRandomMines(minesNumber);
	}

	/**
	 * Inicializa las minas en lugares aleatorios dentro del tablero de juego.
	 * 
	 * @param minesNumber
	 */
	private static void placeRandomMines(int minesNumber) {
		Random random = new Random();
		int count = 0;
		while (count < minesNumber) {
			int row = random.nextInt(ROWS);
			int col = random.nextInt(COLS);
			if (!boardMines[row][col]) {
				boardMines[row][col] = true;
				count++;
			}
		}
	}

	/**
	 * Método que imprime el tablero de juego. Si una casilla ya fue revelada, se
	 * visualizará el valor de las minas cercanas a ella. Si se terminó el juego,
	 * permite mostrar la posición de las bombas.
	 * 
	 * @param showSolution Mostrar solución?
	 */
	private static void printBoard(boolean showSolution) {
		System.out.print("  ");
		for (int j = 0; j < COLS; j++) {
			System.out.print(j + " ");
		}
		System.out.println();

		for (int i = 0; i < ROWS; i++) {
			System.out.print(i + " ");
			for (int j = 0; j < COLS; j++) {
				if (revealed[i][j]) {
					System.out.print(board[i][j] + " ");
				} else if (showSolution && boardMines[i][j]) {
					System.out.print("X" + " ");
				} else {
					System.out.print("* ");
				}
			}
			System.out.println();
		}
	}

	/**
	 * Método que representa la revelación de una casilla. Dada las coordenadas
	 * (fila columna), revisa si existe una mina allí. Si no existe mina, revisa las
	 * minas adyacentes y almacena esa cantidad en su casilla. Si la casilla ya fue
	 * revelada, no realiza operación alguna.
	 * 
	 * @param row fila
	 * @param col columna
	 */
	private static void uncoverCell(int row, int col) {
		if (revealed[row][col]) {
			System.out.println("Ya revisaste esta coordenada");
			return;
		}

		revealed[row][col] = true;
		uncovered++;

		if (boardMines[row][col]) {
			board[row][col] = 'X';
			return;
		}

		int mineProximity = countAdjacentMines(row, col);
		board[row][col] = (char) ('0' + mineProximity);

	}

	/**
	 * Método que permite contar la cantidad de minas adyacentes a la celda
	 * revelada.
	 * 
	 * @param row fila
	 * @param col columna
	 * @return Cantidad de minas adyacentes
	 */
	private static int countAdjacentMines(int row, int col) {
		int count = 0;

		// Ejemplo: si revelo 1,1 , debo evaluar su vecindad desde 0,0 - 0,1 - 0,2 - 1,0
		// - 1,1 - 1,2 - 2,0 - 2,1 - 2,2
		// que es el equivalente a row - 1 a row + 1 y col - 1 a col + 1.
		// [-] [-] [-] [?]
		// [-] [X] [-] [?]
		// [-] [-] [-] [?]
		// [?] [?] [?] [?]
		for (int i = row - 1; i <= row + 1; i++) {
			for (int j = col - 1; j <= col + 1; j++) {
				if (i == row && j == col) {
					continue;
				}

				// Valores de posición negativos no aplican
				if (i >= 0 && i < ROWS && j >= 0 && j < COLS) {
					if (boardMines[i][j]) {
						count++;
					}
				}
			}
		}

		return count;
	}
}
