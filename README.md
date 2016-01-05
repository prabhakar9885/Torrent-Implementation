Commands:
=========
-> Search for the file containing "abc" as a sub-string.
    'SEARCH:abc' (without quotes)
   Expected Output:
      ip_addr:port_number:1_path_to_file_containg_abc_as_substring
      ip_addr:port_number:2_path_to_file_containg_abc_as_substring
      ip_addr:port_number:3_path_to_file_containg_abc_as_substring
      ip_addr:port_number:4_path_to_file_containg_abc_as_substring
      ip_addr:port_number:5_path_to_file_containg_abc_as_substring

-> Download the file
     'PULL:5_path_to_file_containg_abc_as_substring' ( without quotes )
