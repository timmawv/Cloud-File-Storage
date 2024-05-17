function prepareForm(form) {
    const fileInput = form.querySelector('#file');
    const fileValue = fileInput.value;

    const errorElement = form.querySelector('#errorFileName');

    if (fileValue.trim() === "" || !fileValue || fileValue.includes(" ")) {
        errorElement.textContent = "File name can't be empty or contain spaces.";
        return false;
    }

    if (fileValue.includes(".")) {
        errorElement.textContent = "File name can't contain dots.";
        return false;
    }
}