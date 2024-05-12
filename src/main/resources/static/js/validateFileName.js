function prepareForm(form) {
    const fileInput = form.querySelector('input[type="file"]');
    const fileValue = fileInput.value;

    if (!fileValue) {
        document.getElementById("error").textContent = "File name can't be empty.";
        return false;
    }

    if (fileValue.includes(" ") || fileValue.includes(".")) {
        document.getElementById("error").textContent = "File name can't contain dots or spaces.";
        return false;
    }
}
