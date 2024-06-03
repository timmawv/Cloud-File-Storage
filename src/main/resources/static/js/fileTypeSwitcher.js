function switchTypes() {

    const buttonOne = document.querySelector('.btn.btn-outline-primary.one');
    const buttonTwo = document.querySelector('.btn.btn-outline-primary.two');

    const files = document.querySelectorAll(".file.file");
    const directories = document.querySelectorAll(".file.dir");

    buttonOne.addEventListener('click', function () {
        const isActive = buttonOne.classList.contains('active');
        if (!isActive) {
            buttonOne.classList.toggle('active');
            buttonTwo.classList.remove('active');
            files.forEach(function (block) {
                block.style.display = "inline-block";
            });
            directories.forEach(function (block) {
                block.style.display = "none";
            });
        } else {
            buttonOne.classList.toggle('active');
            buttonTwo.classList.remove('active');

            files.forEach(function (block) {
                block.style.display = "inline-block";
            });
            directories.forEach(function (block) {
                block.style.display = "inline-block";
            });
        }
    });

    buttonTwo.addEventListener('click', function () {
        const isActive = buttonTwo.classList.contains('active');
        if (!isActive) {
            buttonOne.classList.remove('active');
            buttonTwo.classList.toggle('active');
            files.forEach(function (block) {
                block.style.display = "none";
            });
            directories.forEach(function (block) {
                block.style.display = "inline-block";
            });
        } else {
            buttonOne.classList.remove('active');
            buttonTwo.classList.toggle('active');

            files.forEach(function (block) {
                block.style.display = "inline-block";
            });
            directories.forEach(function (block) {
                block.style.display = "inline-block";
            });
        }
    });
}