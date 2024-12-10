from pynput.mouse import Button, Controller, Listener
import time
import json
import ast
import subprocess
import os
import boardAnalyzer

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
        time.sleep(0.075)
        
        mouse.press(Button.left)
        
        for i in range(1, len(path)):
            mouse.position = tilesPos[(path[i])]
            time.sleep(0.075)

        mouse.release(Button.left)
        time.sleep(0.075)
    print("Finished solving! Reached end of list.")


def main():
    start_time = time.time()
    grid_size, rowList = boardAnalyzer.main()
    
    rowList = ["".join(row).lower() for row in rowList]

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
    top_left, bottom_right = boardAnalyzer.coords[0], boardAnalyzer.coords[1]

    # Click back into iPhone Mirroring application to refocus
    center_x = (top_left[0] + bottom_right[0]) // 2
    center_y = (top_left[1] + bottom_right[1]) // 2
    mouse.position = (center_x, center_y)
    time.sleep(0.05)
    mouse.press(Button.left)
    mouse.release(Button.left)
    time.sleep(0.05)
    
    # Drag across each row of tiles
    print("Solving...")
    drag_across_tiles(top_left[0], top_left[1], bottom_right[0], bottom_right[1], grid_size, all_word_paths, start_time)

if __name__ == "__main__":
    main()

