input = """
3 7 2 3 4 5 7 8 9 0 0
1 10 1 0 4
1 14 2 0 10 3
1 12 2 0 10 2
1 13 2 0 12 7
1 16 2 0 11 9
1 12 2 0 11 8
1 10 2 0 13 4
1 16 2 0 15 5
1 14 2 0 15 5
1 11 2 0 16 7
1 15 2 0 14 4
1 1 2 0 2 3
1 1 2 0 8 9
1 1 1 1 13
1 1 1 1 16
0
2 ext1
3 ext2
4 ext3
5 ext4
6 ext5
7 ext6
8 ext7
9 ext8
10 int1
11 int2
12 int3
13 int4
14 int5
15 int6
16 int7
0
B+
0
B-
1
0
1
"""
output = """
{ext2, ext3, ext4, ext6, ext7, int1, int2, int3, int4, int5, int6, int7}
"""
