import {useState} from "react"
export default function GenericFormComponent({config}){
    const [ formData, setFormData] = useState({});

    const handleChange = (name, value)=>{
        setFormData((prev)=>({
            ...prev, 
            [name]:value,
        }))
    }

    const handleSubmit = (e)=>{
        e.preventDefault();
        config.actions.submit.onSubmit(formData);
    }
    return (
          <div className="min-h-screen flex items-center justify-center 
                    bg-gradient-to-br from-white via-gray-50 to-gray-100 px-4">

      <div className="w-full max-w-sm bg-white rounded-2xl shadow-xl px-8 py-10">

        {/* Title */}
        <h2 className="text-2xl font-semibold text-center tracking-wide text-gray-900">
          {config.meta.title}
        </h2>

        {/* Subtitle */}
        <p className="mt-2 text-center text-sm text-gray-500">
          {config.meta.subtitle}
        </p>

        {/* Form */}
        <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
          {config.fields.map((field) => (
            <div key={field.name}>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                {field.label}
              </label>

              <input
                type={field.type}
                required={field.required}
                placeholder={field.placeholder}
                value={formData[field.name] || ""}
                onChange={(e) =>
                  handleChange(field.name, e.target.value)
                }
                className="w-full rounded-lg border border-gray-300 px-4 py-2
                           text-gray-700 focus:outline-none focus:ring-2
                           focus:ring-black focus:border-transparent"
              />
            </div>
          ))}

          {/* Submit Button */}
          {config.actions.submit.show && (
            <button
              type={config.actions.type}
              className="w-full rounded-xl bg-black py-3 text-white
                         font-medium tracking-wide hover:bg-gray-900
                         transition active:scale-[0.98] shadow-md"
            >
              {config.actions.submit.text}
            </button>
          )}
        </form>
      </div>
    </div>
    )
}