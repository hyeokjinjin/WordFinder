from pynput.mouse import Button, Controller
import time
import json
import ast
import subprocess

# Initialize the mouse controller
mouse = Controller()

# Function to wait for Enter key press and capture mouse position
def wait_for_key_and_capture_position():
    input("Press Enter to capture corner position")
    x, y = mouse.position
    print(f"Mouse position captured at: ({x}, {y})")
    return (x, y)

# Function to drag across each row of tiles
def drag_across_tiles(x1, y1, x2, y2, grid_size, word_paths, start_time):
    tile_width = (x2 - x1) // grid_size
    tile_height = (y2 - y1) // grid_size
    
    tilesPos = {}
    
    for row in range(grid_size):
        start_x = x1 + (tile_width // 2)
        start_y = y1 + (row * tile_height) + (tile_height // 2)
        
        tilesPos[(0, row)] = (start_x, start_y)
        
        for col in range(1, grid_size):
            tile_x = start_x + (col * tile_width)
            tile_y = start_y
            
            tilesPos[(col, row)] = (tile_x, tile_y)
    
    for path in word_paths:
        if ((time.time() - start_time) > 70):
            print("Stopping due to time limit")
            break
        mouse.position = (tilesPos[(path[0])])
        time.sleep(0.1)
        
        mouse.press(Button.left)
        
        for i in range(1, len(path)):
            mouse.position = tilesPos[(path[i])]
            time.sleep(0.1)

        mouse.release(Button.left)
        time.sleep(0.1)

def main():
    subprocess.run(['javac', '-cp', 'lib/*', 'Graph.java', 'Trie.java', 'Launch.java'])
    subprocess.run(['java', '-cp', '.:lib/*', 'Launch'])

    # Start the timer
    start_time = time.time()
    
    # Reading JSON file and converting paths to tuple list
    # Open json
    with open("savedNodes.json", "r") as file:
        data = json.load(file)
        
    # Save the boardSize to the variable 'size'
    grid_size = data['boardSize']

    # Initialize an empty list to store all word path lists
    all_word_paths = []

    # Convert word paths and add them to the list
    for word, path in data['wordPaths'].items():
        # Convert coordinates from string to tuple
        converted_path = [ast.literal_eval(coord) for coord in path]
        all_word_paths.append(converted_path)


    # Getting user input for board size and location
    (x1, y1) = wait_for_key_and_capture_position()
    (x2, y2) = wait_for_key_and_capture_position()
    
    # Calculate rectangle corners
    top_left = (min(x1, x2), min(y1, y2))
    bottom_right = (max(x1, x2), max(y1, y2))
    
    center_x = (top_left[0] + bottom_right[0]) // 2
    center_y = (top_left[1] + bottom_right[1]) // 2
    mouse.position = (center_x, center_y)
    mouse.press(Button.left)
    mouse.release(Button.left)
    
    # Drag across each row of tiles
    drag_across_tiles(top_left[0], top_left[1], bottom_right[0], bottom_right[1], grid_size, all_word_paths, start_time)

if __name__ == "__main__":
    main()

