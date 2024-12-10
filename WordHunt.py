from pynput.mouse import Button, Controller, Listener
import time
import json
import ast
import subprocess
import os

# Initialize the mouse controller and coords list for two corners of board
mouse = Controller()
coords = []


# Listener to capture mouse click positions and store them in a list.
# Stop listening after two clicks.
def on_click(x, y, button, pressed):
    if pressed:
        print(f"Mouse clicked at ({x}, {y})")
        coords.append((x, y))
        if len(coords) == 2:
            return False  # Stop the listener after two clicks


# Wait for two mouse clicks to capture the top-left and bottom-right corners.
def capture_two_corners():
    print("Click the top-left corner of the board.")
    print("Then click the bottom-right corner of the board.")
    with Listener(on_click=on_click) as listener:
        listener.join()
    
    if len(coords) != 2:
        raise ValueError("Failed to capture two corners.")
    
    return coords[0], coords[1]


# Function to automate dragging across tiles for word combinations
def drag_across_tiles(x1, y1, x2, y2, grid_size, word_paths, start_time):
    tile_width = (x2 - x1) // grid_size
    tile_height = (y2 - y1) // grid_size
    
    tilesPos = {}
    
    # Populate tilesPos dictionary with grid coordinate as key and mouse cursor coordinates as value
    for row in range(grid_size):
        start_x = x1 + (tile_width // 2)
        start_y = y1 + (row * tile_height) + (tile_height // 2)
        
        tilesPos[(0, row)] = (start_x, start_y)
        
        for col in range(1, grid_size):
            tile_x = start_x + (col * tile_width)
            tile_y = start_y
            
            tilesPos[(col, row)] = (tile_x, tile_y)
    
    # Move mouse cursor automatically for each word in file
    for path in word_paths:
        if ((time.time() - start_time) > 80):
            print("Stopping due to time limit")
            os._exit(0)
            break
        mouse.position = (tilesPos[(path[0])])
        time.sleep(0.1)
        
        mouse.press(Button.left)
        
        for i in range(1, len(path)):
            mouse.position = tilesPos[(path[i])]
            time.sleep(0.1)

        mouse.release(Button.left)
        time.sleep(0.1)
    print("End of List")


def main():
    # Get input for board size
    grid_size = -1
    while grid_size == -1:
        try:
            inputValue = int(input("Enter board size (4 or 5): "))
            if (inputValue != 4 and inputValue != 5):
                raise ValueError
            grid_size = inputValue
        except ValueError:
            print("Invalid input. Please enter the size of the game board.")
    
    rowList = []
    
    i = 0
    while i < grid_size:
        try:
            rowLetters = input(f"Enter row {i + 1} letters: ").strip()
            if len(rowLetters) != grid_size:
                raise ValueError
            rowList.append(rowLetters)
            i += 1
            if i == 1:
                # Start the timer after the first row input
                start_time = time.time()
        except ValueError:
            print(f"Invalid row input. Please enter the {grid_size} letters of row {i + 1}")
    
    # Encode the list of rows as a json file to send to Java
    encodedList = json.dumps(rowList)
    
    # Executing Java for DFS and word list answer key
    subprocess.run(['javac', '-cp', 'lib/*', 'Graph.java', 'Trie.java', 'Launch.java'])
    subprocess.run(['java', '-cp', '.:lib/*', 'Graph', str(grid_size), encodedList])
    
    # Reading JSON file and converting paths to tuple list
    with open("savedNodes.json", "r") as file:
        data = json.load(file)

    # Initialize an empty list to store all word path lists
    all_word_paths = []

    # Convert word paths and add them to the list
    for word, path in data['wordPaths'].items():
        # Convert coordinates from string to tuple
        converted_path = [ast.literal_eval(coord) for coord in path]
        all_word_paths.append(converted_path)

    # Getting user input for board size and location
    top_left, bottom_right = capture_two_corners()
    
    # Click back into iPhone Mirroring application to refocus
    center_x = (top_left[0] + bottom_right[0]) // 2
    center_y = (top_left[1] + bottom_right[1]) // 2
    mouse.position = (center_x, center_y)
    mouse.press(Button.left)
    mouse.release(Button.left)
    
    # Drag across each row of tiles
    drag_across_tiles(top_left[0], top_left[1], bottom_right[0], bottom_right[1], grid_size, all_word_paths, start_time)

if __name__ == "__main__":
    main()

