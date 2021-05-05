package tech.skkot.model

import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.Test
import tech.skot.model.AndroidSKPersistor
import kotlin.test.assertTrue
import kotlinx.serialization.Serializable
import tech.skot.model.SKPersistor

class TestSKPersistor {

    @Test
    fun testBasicKeyStringJobs() {
        runBlocking {
            val persistor = AndroidSKPersistor(InstrumentationRegistry.getInstrumentation().context, "testpersistor")

            val name = "test"
            val initialValue = persistor.getString(name)
            assertTrue("Initial value is null") {
                initialValue == null
            }
            val settedData = "Hello !"
            persistor.putString(name, settedData)
            val savedValue = persistor.getString(name)
            assertTrue("Put data is well saved") {
                savedValue?.data == settedData
            }

            val secondName = "nameTest2"
            val settedSecondData = "Hello test 2"
            persistor.putString(secondName, settedSecondData)
            persistor.getString(name).let {
                assertTrue("First name value still ok") {
                    it?.data == settedData
                }
            }
            persistor.getString(secondName).let {
                assertTrue("Second name value is well saved") {
                    it?.data == settedSecondData
                }
            }

        }
    }


    @Test
    fun testStringJobsWithKeys() {
        runBlocking {
            val persistor = AndroidSKPersistor(InstrumentationRegistry.getInstrumentation().context, "testpersistor")

            val name1 = "name1"
            val value1 = "value1"

            val anotherKey = "anotherKey"

            val value2 = "value2"

            persistor.putString(name1, value1)
            persistor.getString(name = name1, key = null).let {
                assertTrue("Default id is null") {
                    value1 == it?.data
                }
            }

            persistor.getString(name = name1, key = anotherKey).let {
                assertTrue("Wrong key return null") {
                    it == null
                }
            }

            persistor.putString(name1, value2, anotherKey)
            persistor.getString(name = name1, key = null).let {
                assertTrue("Setting value with new key old key -> nulll") {
                    it == null
                }
            }

            persistor.getString(name = name1, key = anotherKey).let {
                assertTrue("new key -> good value") {
                    it?.data == value2
                }
            }

        }
    }


    @Test
    fun testPersistedOnDisk() {
        runBlocking {
            val name = "name"
            val value = "value"
            val fileName = "testPersistent"

            val persistor1 = AndroidSKPersistor(InstrumentationRegistry.getInstrumentation().context, fileName)
            persistor1.putString(name, value)

            val persistor2 = AndroidSKPersistor(InstrumentationRegistry.getInstrumentation().context, fileName)
            persistor2.getString(name).let {
                assertTrue("New Persistor with same file name find value saved before") {
                    it?.data == value
                }
            }
        }
    }

    @Test
    fun testClearAll() {
        runBlocking {
            val name = "name"
            val value = "value"
            val fileName = "testClear"

            val persistor1 = AndroidSKPersistor(InstrumentationRegistry.getInstrumentation().context, fileName)
            persistor1.putString(name, value)

            persistor1.getString(name).let {
                assertTrue("value well saved") {
                    it?.data == value
                }
            }
            persistor1.clear()
            persistor1.getString(name).let {
                assertTrue("data well cleared") {
                    it == null
                }
            }

            persistor1.putString(name, value)

            persistor1.getString(name).let {
                assertTrue("value well saved") {
                    it?.data == value
                }
            }
            persistor1.clear()

            val persistor2 = AndroidSKPersistor(InstrumentationRegistry.getInstrumentation().context, fileName)
            persistor2.getString(name).let {
                assertTrue("New Persistor with same file name don't find cleared value") {
                    it == null
                }
            }
        }
    }

    @Test
    fun testRemoveOneKey() {
        runBlocking {
            val name = "name"
            val value = "value"
            val name2 = "name2"
            val value2 = "value2"

            val fileName = "testClear"

            val persistor1 = AndroidSKPersistor(InstrumentationRegistry.getInstrumentation().context, fileName)
            persistor1.putString(name, value)
            persistor1.putString(name2, value2)

            persistor1.getString(name).let {
                assertTrue("value well saved") {
                    it?.data == value
                }
            }
            persistor1.getString(name2).let {
                assertTrue("value2 well saved") {
                    it?.data == value2
                }
            }
            persistor1.remove(name)
            persistor1.getString(name).let {
                assertTrue("data well cleared") {
                    it == null
                }
            }
            persistor1.getString(name2).let {
                assertTrue("value2 not affected") {
                    it?.data == value2
                }
            }

        }
    }

    @Serializable
    data class SubObject(val oneField: String)

    @Serializable
    data class TestObject(val field1: String, val field2: Int, val field3: SubObject)


    @Test
    fun testSavingObjects() {
        runBlocking {
            val name = "name"
            val value = TestObject(
                    field1 = "test",
                    field2 = 3,
                    field3 = SubObject("coucou")
            )

            val key = "key"
            val otherKey = "otherKey"

            val fileName = "testSavingObjects"
            val persistor:SKPersistor = AndroidSKPersistor(InstrumentationRegistry.getInstrumentation().context, fileName)

            persistor.putData(TestObject.serializer(), name, value)

            persistor.getData(TestObject.serializer(), name).let {
                assertTrue {
                    it?.data == value
                }
            }

            persistor.getData(TestObject.serializer(), name, key = key).let {
                assertTrue {
                    it == null
                }
            }

            persistor.getData(TestObject.serializer(), name).let {
                assertTrue {
                    it?.data == value
                }
            }

            persistor.putData(TestObject.serializer(), name, value, key)
            persistor.getData(TestObject.serializer(), name, key).let {
                assertTrue {
                    it?.data == value
                }
            }
            persistor.getData(TestObject.serializer(), name).let {
                assertTrue {
                    it == null
                }
            }
            persistor.getData(TestObject.serializer(), name, otherKey).let {
                assertTrue {
                    it == null
                }
            }

        }
    }


}