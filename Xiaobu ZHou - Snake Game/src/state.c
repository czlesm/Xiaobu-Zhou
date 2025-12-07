#include "state.h"

#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "snake_utils.h"

/* Helper function definitions */
static void set_board_at(game_state_t *state, unsigned int row, unsigned int col, char ch);
static bool is_tail(char c);
static bool is_head(char c);
static bool is_snake(char c);
static char body_to_tail(char c);
static char head_to_body(char c);
static unsigned int get_next_row(unsigned int cur_row, char c);
static unsigned int get_next_col(unsigned int cur_col, char c);
static void find_head(game_state_t *state, unsigned int snum);
static char next_square(game_state_t *state, unsigned int snum);
static void update_tail(game_state_t *state, unsigned int snum);
static void update_head(game_state_t *state, unsigned int snum);


/* Task 1 */
game_state_t *create_default_state() {
  // TODO: Implement this function.


  game_state_t* state = malloc(sizeof(game_state_t));

  if (!state) {
    return NULL;
  }
  state->num_rows = 18;
  long unsigned int col = 21;

  state->board = malloc(state->num_rows * sizeof(char*));
  if (!state->board) {
    free(state);
    return NULL;
  }

  for (int i = 0; i < state->num_rows; i++) {
    state->board[i] = malloc(col * sizeof(char));
    if (!state->board[i]) {
      for (int j = 0; j < i; j++) free(state->board[j]);
      free(state->board);
      free(state);
      return NULL;
    }

    for (int j = 0; j < col - 1; j++) {
      state->board[i][j] = (i == 0 || i == state->num_rows - 1 || j == 0 || j == col - 2) ? '#' : ' ';
      if (i == 2 && j == 9) state->board[i][j] = '*';
    }
    state->board[i][col - 1] = '\0';
  }

  state->num_snakes = 1;
  state->snakes = malloc(state->num_snakes * sizeof(snake_t));
  if (!state->snakes) {
    for (int i = 0; i < state->num_rows; i++) free(state->board[i]);
    free(state->board);
    free(state);
    return NULL;
  }

  state->snakes[0].head_row = 2;
  state->snakes[0].head_col = 4;
  state->snakes[0].tail_row = 2;
  state->snakes[0].tail_col = 2;
  state->snakes[0].live = true;

  state->board[2][4] = 'D';
  state->board[2][2] = 'd';
  state->board[2][3] = '>';
  return state;
}


/* Task 2 */
void free_state(game_state_t *state) {
  // TODO: Implement this function.

  if (state == NULL) return;

  if (state->snakes != NULL) {
    free(state->snakes);
    state->snakes = NULL;
  }

  if (state->board != NULL) {
    for (unsigned int i = 0; i < state->num_rows; i++) {
      free(state->board[i]);
      state->board[i] = NULL;
    }
    free(state->board);
    state->board = NULL;
  }

  free(state);
}


/* Task 3 */
void print_board(game_state_t *state, FILE *fp) {
  // TODO: Implement this function.

  for(unsigned int i = 0; i < state->num_rows; i++) {
    fprintf(fp, "%s\n", state->board[i]);
  }

  return;
}

/*
  Saves the current state into filename. Does not modify the state object.
  (already implemented for you).
*/
void save_board(game_state_t *state, char *filename) {
  FILE *f = fopen(filename, "w");
  print_board(state, f);
  fclose(f);
}

/* Task 4.1 */

/*
  Helper function to get a character from the board
  (already implemented for you).
*/
char get_board_at(game_state_t *state, unsigned int row, unsigned int col) { return state->board[row][col]; }

/*
  Helper function to set a character on the board
  (already implemented for you).
*/
static void set_board_at(game_state_t *state, unsigned int row, unsigned int col, char ch) {
  state->board[row][col] = ch;
}

/*
  Returns true if c is part of the snake's tail.
  The snake consists of these characters: "wasd"
  Returns false otherwise.
*/
static bool is_tail(char c) {
  // TODO: Implement this function.

  if (c == 'w' || c == 'a' || c == 's' || c == 'd')
    return true;

  else
    return false;
}

/*
  Returns true if c is part of the snake's head.
  The snake consists of these characters: "WASDx"
  Returns false otherwise.
*/
static bool is_head(char c) {
  // TODO: Implement this function.
  if (c == 'W' || c == 'A' || c == 'S' || c == 'D' || c == 'x')
    return true;
  else
    return false;

}

/*
  Returns true if c is part of the snake.
  The snake consists of these characters: "wasd^<v>WASDx"
*/
static bool is_snake(char c) {
  // TODO: Implement this function.
  if (is_head(c) || is_tail(c) || c == '<' || c == '^' || c == 'v' || c == '>')
    return true;
  return false;
}

/*
  Converts a character in the snake's body ("^<v>")
  to the matching character representing the snake's
  tail ("wasd").
*/
static char body_to_tail(char c) {
  // TODO: Implement this function.
  if (c == '^')
    return 'w';

  else if (c == '<')
    return 'a';
  
  else if (c == 'v')
    return 's';

  else if (c == '>')
    return 'd';

  return '\0';
}


/*
  Converts a character in the snake's head ("WASD")
  to the matching character representing the snake's
  body ("^<v>").
*/
static char head_to_body(char c) {
  // TODO: Implement this function.
  if (c == 'W')
    return '^';

  else if (c == 'A')
    return '<';

  else if (c == 'S')
    return 'v';

  else if (c == 'D')
    return '>';

  return '\0';
}

/*
  Returns cur_row + 1 if c is 'v' or 's' or 'S'.
  Returns cur_row - 1 if c is '^' or 'w' or 'W'.
  Returns cur_row otherwise.
*/
static unsigned int get_next_row(unsigned int cur_row, char c) {
  // TODO: Implement this function.
  if(c=='v'||c=='s'||c=='S') return cur_row+1;
  if(c=='^'||c=='w'||c=='W') return cur_row-1;

  return cur_row;
}

/*
  Returns cur_col + 1 if c is '>' or 'd' or 'D'.
  Returns cur_col - 1 if c is '<' or 'a' or 'A'.
  Returns cur_col otherwise.
*/
static unsigned int get_next_col(unsigned int cur_col, char c) {
  // TODO: Implement this function.
  if(c=='>'||c=='d'||c=='D') return cur_col+1;
  if(c=='<'||c=='a'||c=='A') return cur_col-1;

  return cur_col;
}

/*
  Task 4.2

  Helper function for update_state. Return the character in the cell the snake is moving into.

  This function should not modify anything.
*/
static char next_square(game_state_t *state, unsigned int snum) {
  // TODO: Implement this function.

  unsigned int next_row = get_next_row(state->snakes[snum].head_row, get_board_at(state, state->snakes[snum].head_row, state->snakes[snum].head_col));
  unsigned int next_col = get_next_col(state->snakes[snum].head_col, get_board_at(state, state->snakes[snum].head_row, state->snakes[snum].head_col));
  return get_board_at(state, next_row, next_col);
}

/*
  Task 4.3

  Helper function for update_state. Update the head...

  ...on the board: add a character where the snake is moving

  ...in the snake struct: update the row and col of the head

  Note that this function ignores food, walls, and snake bodies when moving the head.
*/
static void update_head(game_state_t *state, unsigned int snum) {
  // TODO: Implement this function.
  char direction = get_board_at(state, state->snakes[snum].head_row, state->snakes[snum].head_col);

  state->board[state->snakes[snum].head_row][state->snakes[snum].head_col] = head_to_body(direction);

  state->snakes[snum].head_row = get_next_row(state->snakes[snum].head_row, direction);

  state->snakes[snum].head_col = get_next_col(state->snakes[snum].head_col, direction);

  state->board[state->snakes[snum].head_row][state->snakes[snum].head_col] = direction;

}

/*
  Task 4.4

  Helper function for update_state. Update the tail...

  ...on the board: blank out the current tail, and change the new
  tail from a body character (^<v>) into a tail character (wasd)

  ...in the snake struct: update the row and col of the tail
*/
static void update_tail(game_state_t *state, unsigned int snum) {
  // TODO: Implement this function.
  char direction = get_board_at(state, state->snakes[snum].tail_row, state->snakes[snum].tail_col);

  state->board[state->snakes[snum].tail_row][state->snakes[snum].tail_col] = ' ';

  state->snakes[snum].tail_row = get_next_row(state->snakes[snum].tail_row, direction);

  state->snakes[snum].tail_col = get_next_col(state->snakes[snum].tail_col, direction);

  state->board[state->snakes[snum].tail_row][state->snakes[snum].tail_col] = body_to_tail(get_board_at(state, state->snakes[snum].tail_row, state->snakes[snum].tail_col));
}

/* Task 4.5 */
void update_state(game_state_t *state, int (*add_food)(game_state_t *state)) {
  // TODO: Implement this function.
  for(int i = 0; i < state->num_snakes; i++) {
    if (!state->snakes[i].live) continue;
    char next = next_square(state, i);
    switch (next) {
      case ' ':
        update_head(state, i);
        update_tail(state, i);
        break;
      case '*':
        update_head(state, i);
        add_food(state);
        break;
      default:
        state->snakes[i].live = false;
        set_board_at(state, state->snakes[i].head_row, state->snakes[i].head_col, 'x');
    }
  }
}

/* Task 5.1 */
char *read_line(FILE *fp) {
  // TODO: Implement this function.

    if (fp == NULL) {
        return NULL;
    }
    // Initial capacity
    unsigned int capacity = 10;
    // Allocate initial buffer
    char *lineBuffer = (char *)malloc(sizeof(char) * capacity);
    if (lineBuffer == NULL) return NULL;
    
    // Attempt to read the first chunk of the line
    if (fgets(lineBuffer, capacity, fp) == NULL) {
        free(lineBuffer);
        return NULL;
    }

    while (strchr(lineBuffer, '\n') == NULL) {
        // Increase capacity
        capacity *= 2;
        char *tempBuffer = realloc(lineBuffer, capacity);
        if (tempBuffer == NULL) {
            free(lineBuffer);
            return NULL;
        }
        lineBuffer = tempBuffer;

        if (!fgets(lineBuffer + strlen(lineBuffer), capacity - strlen(lineBuffer), fp)) {
            if (ferror(fp)) { // Check for read error
                free(lineBuffer);
                return NULL;
            }
            break; 
        }
    }

    return lineBuffer;
}
    



/* Task 5.2 */
game_state_t *load_board(FILE *fp) {
  // TODO: Implement this function.
    if (fp == NULL) {
        return NULL;
    }

    game_state_t *game_state = malloc(sizeof(game_state_t));
    if (game_state == NULL) {
        return NULL; // Memory allocation failed
    }

    game_state->num_snakes = 0;
    game_state->snakes = NULL;
    game_state->board = NULL;
    unsigned int rows = 0;

    char *current_line;
    while ((current_line = read_line(fp)) != NULL) {
        size_t line_length = strlen(current_line);
        
        // Check and remove the newline character at the end if present
        if (line_length > 0 && current_line[line_length - 1] == '\n') {
            current_line[line_length - 1] = '\0'; // Remove newline character
        }

        char **new_board = realloc(game_state->board, sizeof(char*) * (rows + 1));
        if (!new_board) {
            // Clean up allocated memory on failure
            for (unsigned int i = 0; i < rows; i++) {
                free(game_state->board[i]);
            }
            free(game_state->board);
            free(game_state);
            free(current_line);
            return NULL;
        }

        game_state->board = new_board;
        game_state->board[rows++] = current_line; // Store the new line
    }

    game_state->num_rows = rows;
    return game_state;
}









/*
  Task 6.1

  Helper function for initialize_snakes.
  Given a snake struct with the tail row and col filled in,
  trace through the board to find the head row and col, and
  fill in the head row and col in the struct.
*/
static void find_head(game_state_t *state, unsigned int snum) {
  // TODO: Implement this function.
    unsigned int counter = 0;
    unsigned int row = 0, col = 0; 

    // find the tail of the snake
    for (unsigned int i = 0; i < state->num_rows; i++) {
        for (unsigned int j = 0; state->board[i][j] != '\0'; j++) {
            char currentChar = get_board_at(state, i, j); // Fetch at the current position
            if (is_tail(currentChar)) {
                if (counter == snum) {
                    
                    row = i;
                    col = j;
                    goto FOUND_TAIL; 
                }
                counter++; 
            }
        }
    }
FOUND_TAIL:
    // follow the snake from its tail to its head.
    char direction = get_board_at(state, row, col); // Starting direction based on the tail.

    while (!is_head(direction)) {
        //return the next position to move to.
        row = get_next_row(row, direction);
        col = get_next_col(col, direction);

    
        direction = get_board_at(state, row, col);

        // Avoid infinite loop
        if (row >= state->num_rows || col >= strlen(state->board[row]) || direction == ' ') {
            
            break;
        }
    }

    // Update the snake structure with the head's found position.
    state->snakes[snum].head_row = row;
    state->snakes[snum].head_col = col;
}




/* Task 6.2 */
game_state_t *initialize_snakes(game_state_t *state) {
   // TODO: Implement this function.
    unsigned int num_snakes = 0;
    
    // find all tails to count num of snakes
    for (unsigned int i = 0; i < state->num_rows; i++) {
        for (unsigned int j = 0; j < strlen(state->board[i]); j++) {
            if (is_tail(state->board[i][j])) {
                num_snakes++;
            }
        }
    }
    
    // Allocate memory for snake structs
    state->snakes = (snake_t*)malloc(num_snakes * sizeof(snake_t));
    state->num_snakes = num_snakes;
    

    num_snakes = 0;
    
    // initialize each snake
    for (unsigned int i = 0; i < state->num_rows; i++) {
        for (unsigned int j = 0; j < strlen(state->board[i]); j++) {
            char current = state->board[i][j];
            if (is_tail(current)) {
                state->snakes[num_snakes].tail_row = i;
                state->snakes[num_snakes].tail_col = j;
                state->snakes[num_snakes].live = true; // Assume snake is alive
                find_head(state, num_snakes); 
                num_snakes++;
            }
        }
    }

    return state;
}
