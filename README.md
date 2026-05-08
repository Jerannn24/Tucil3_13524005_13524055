# Ancient Ruins Explorer: The Lost Paths of the Relic
# TUGAS KECIL 3 - STRATEGI ALGORITMA
## Deskripsi
Program ini membandingkan beberapa strategi pencarian rute (pathfinding), termasuk UCS, GBFS, A*, BFS, dan DFS, untuk mengungkapkan bagaimana perilaku dari setiap algoritma saat rutenya sempit, mahal, atau penuh jalan pintas yang menipu. Mode konsol dan mode grafis disediakan sehingga perjalanan dapat diperiksa langkah demi langkah.

## Anggota Tim
- Geraldo Artemius - 13524005
- Junior Natra Situmorang - 13524055

## Cara Menjalankan Program
Proyek ini menggunakan Java. File `.class` hasil kompilasi akan diletakkan di dalam direktori `bin`. Pastikan Anda menjalankan perintah dari root direktori proyek, atau arahkan ke `bin` jika menjalankan secara independen, agar jalur file ke `test/input`, `test/iteration`, dan `test/output` dapat bekerja dengan benar.

### 1. Kompilasi file source
Dari root direktori proyek:
```bash
javac -d bin src/*.java
```

### 2. Jalankan versi konsol
```bash
java -cp bin Main
```

### 3. Jalankan versi grafis (GUI)
```bash
java -cp bin GUIMain
```

### 4. Masukkan input saat diminta
Ketika program meminta file input, ketikkan hanya nama file yang berada di dalam `test/input/`.

Contoh:
```text
test1.txt
```

### 5. File output opsional
- Log iterasi akan ditulis ke `test/iteration/`
- Hasil penyelesaian (solusi) yang disimpan akan ditulis ke `test/output/`

## Struktur Proyek
```text
/
├── src/
│   ├── Algorithm.java
│   ├── GUIMain.java
│   ├── Main.java
│   ├── Map.java
│   └── State.java
├── test/
│   ├── input/
│   ├── iteration/
│   └── output/
├── README.md
└── LICENSE
```

## Deskripsi File
- `src/Main.java` - Titik masuk dari mode konsol. Menangani pemilihan test case, pemilihan algoritma, memutar ulang visualisasi konsol, dan menyimpan solusi.
- `src/GUIMain.java` - Antarmuka grafis berbasis Swing untuk melakukan visualisasi peta, hasil algoritma, peta iterasi tahap demi langkah.
- `src/Algorithm.java` - Logika inti penelusuran graf/pohon untuk algoritma UCS, GBFS, A*, BFS, dan DFS.
- `src/Map.java` - Pembaca input dan validator untuk ukuran peta, ubin (tiles), target, dan matriks cost (biaya).
- `src/State.java` - Representasi node pencarian yang digunakan oleh algoritma penelusuran jalur.
- `test/input/` - Tempat penyimpanan file input peta (map).
- `test/iteration/` - Trace dari hasil iterasi algoritma pathfinding.
- `test/output/` - File output yang berisi solusi pathfinding pengguna.
- `README.md` - Gambaran proyek dan petunjuk penggunaan.

## Input dan Output

### Input
Program mengharapkan file teks berekstensi .txt dengan format berikut:
1. Baris pertama: dua integer `N M` yang mewakili jumlah baris dan kolom.
2. `N` baris berikutnya: peta grid.
	- `X` = dinding (wall)
	- `*` = jalan terbuka (path)
	- `Z` = posisi awal (titik mulai), tepat satu
	- `O` = posisi target akhir (tujuan), tepat satu
	- `0`, `1`, `2`, ... = target angka yang secara opsional harus dilewati secara berurutan.
3. `N` baris selanjutnya: matriks ukuran cost grid `N x M` bilangan bulat.

### Output
Program akan mencetak:
- Jalur/rute solusi panjang, jika ditemukan
- Total cost solusi/rute terpendek yang dilalui
- Tampilan keadaan papan untuk setiap tahap penyelesaian rute (playback)
- Waktu eksekusi yang ditempuh algoritma pathfinding 
- Total angka/banyak proses iterasi dari algoritma (state evaluation)

Lalu, jika pengguna memilih untuk menyimpan output/solusi yang berjalan, maka program akan mencatat output ke dalam sebuah file solusi di output/test yang berupa solusi pergerakan (snapshots iterasi) dari tahap ke tahap secara logis.

### Contoh Input
```text
5 8
XXXXXXXX
XZ**0**X
X*XX*X*X
X***1*OX
XXXXXXXX
999 999 999 999 999 999 999 999
999 1 2 3 4 5 6 999
999 2 999 999 3 999 4 999
999 2 2 2 3 4 2 999
999 999 999 999 999 999 999 999
```

## Catatan Tambahan
- Peta/grid harus memiliki tepat satu titik permulaan (`Z`) dan satu titik hasil/tujuan (`O`).
- Jika sebuah peta/grid mengandung input digit (`0`, `1`, `2`, dst... ), seluruh urutan target angka tersebut harus dilalui secara benar dan berurutan dari angka terendah ke angka tertinggi tanpa melompat/melewati angka apa pun.
- Dinding grid matriks cost yang berupa variabel `X` diberikan value penalty dengan nilai cost sangat besar di file input (Contoh: `999`). 
- Menjalankan file secara GUI akan sangat menyenangkan apabila kalian bisa berinteraksi di dalam panel map GUI. 
- Penting diingat: Saat menjalankan map program disarankan pastikan bahwa anda mengarah ke folder awal proyek (root project) sehingga path akses folder input dan folder outputnya akan benar termuat.